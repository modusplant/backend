package kr.modusplant.domains.member.framework.outbound.redis;

import kr.modusplant.domains.comment.framework.outbound.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.outbound.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.domain.vo.ActivitySubjectCommentId;
import kr.modusplant.domains.member.usecase.port.repository.ActivitySubjectPostCacheRepository;
import kr.modusplant.shared.exception.ConnectionFailedException;
import kr.modusplant.shared.exception.ExistsValueException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EXISTS_CHOSEN_COMMENT;
import static kr.modusplant.shared.exception.enums.GeneralErrorCode.CONNECTION_FAILED;

@Repository
@RequiredArgsConstructor
public class ActivitySubjectPostCacheRedisRepository implements ActivitySubjectPostCacheRepository {
    private final StringRedisTemplate stringRedisTemplate;
    private final CommentJpaRepository commentJpaRepository;

    // choose:post:{ulid}:comment-path - 게시글에서 채택된 댓글의 경로
    private static final String CHOSEN_COMMENT_KEY_FORMAT = "choose:post:%s:comment-path";
    private static final Duration TTL = Duration.ofHours(1);

    @Override
    public boolean hasChosenCommentWithDifferentPath(ActivitySubjectCommentId activitySubjectCommentId) {
        String postId = activitySubjectCommentId.getActivitySubjectPostId().getValue();
        String path = activitySubjectCommentId.getActivitySubjectCommentPath().getValue();

        try {
            Optional<String> optionalChosenPath = findChosenPathFromCache(postId);
            if (optionalChosenPath.isEmpty()) {
                optionalChosenPath = findChosenPathFromDBAndCache(postId);
            }
            return optionalChosenPath.isPresent() && !optionalChosenPath.orElseThrow().equals(path);
        } catch (DataAccessException e) {
            throw new ConnectionFailedException(CONNECTION_FAILED, "redis", e);
        }
    }

    // 채택 직전 SET NX로 게시글의 채택 경로를 원자적으로 선점하며, 다른 경로가 이미 선점된 경우에만 예외 발생(재채택은 통과)
    @Override
    public void cacheBeforeChoose(ActivitySubjectCommentId activitySubjectCommentId) {
        String chosenCommentKey = CHOSEN_COMMENT_KEY_FORMAT.formatted(
                activitySubjectCommentId.getActivitySubjectPostId().getValue());
        String path = activitySubjectCommentId.getActivitySubjectCommentPath().getValue();

        boolean chooseable;
        try {
            chooseable = Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(chosenCommentKey, path, TTL))
                    || path.equals(stringRedisTemplate.opsForValue().get(chosenCommentKey));
        } catch (DataAccessException e) {
            throw new ConnectionFailedException(CONNECTION_FAILED, "redis", e);
        }
        if (!chooseable) {
            throw new ExistsValueException(EXISTS_CHOSEN_COMMENT, "activitySubjectCommentPath", path);
        }
    }

    @Override
    public void removeChooseCache(ActivitySubjectCommentId activitySubjectCommentId) {
        String chosenCommentKey = CHOSEN_COMMENT_KEY_FORMAT.formatted(
                activitySubjectCommentId.getActivitySubjectPostId().getValue());
        String path = activitySubjectCommentId.getActivitySubjectCommentPath().getValue();

        try {
            if (path.equals(stringRedisTemplate.opsForValue().get(chosenCommentKey))) {
                stringRedisTemplate.delete(chosenCommentKey);
            }
        } catch (DataAccessException e) {
            throw new ConnectionFailedException(CONNECTION_FAILED, "redis", e);
        }
    }

    private Optional<String> findChosenPathFromCache(String postId) {
        String chosenCommentKey = CHOSEN_COMMENT_KEY_FORMAT.formatted(postId);
        return Optional.ofNullable(stringRedisTemplate.opsForValue().getAndExpire(chosenCommentKey, TTL));
    }

    // 캐시가 비어 있을 때 comm_comment의 is_chosen으로부터 채택된 댓글 경로를 직접 조회하고, 존재하면 캐시에 등록
    private Optional<String> findChosenPathFromDBAndCache(String postId) {
        Optional<String> optionalChosenPath = commentJpaRepository.findFirstByPostUlidAndIsChosenTrue(postId)
                .map(CommentEntity::getPath);
        optionalChosenPath.ifPresent(chosenPath -> {
            String chosenCommentKey = CHOSEN_COMMENT_KEY_FORMAT.formatted(postId);
            stringRedisTemplate.opsForValue().set(chosenCommentKey, chosenPath, TTL);
        });
        return optionalChosenPath;
    }
}
