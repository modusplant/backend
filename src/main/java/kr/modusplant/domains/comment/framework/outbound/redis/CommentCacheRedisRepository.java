package kr.modusplant.domains.comment.framework.outbound.redis;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.port.repository.CommentCacheRepository;
import kr.modusplant.shared.exception.ConnectionFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntSupplier;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.CONNECTION_FAILED;

@Repository
@RequiredArgsConstructor
public class CommentCacheRedisRepository implements CommentCacheRepository {
    private final StringRedisTemplate stringRedisTemplate;

    // post:{ulid}:comment-path:{path}:member-uuid:{uuid} - 작성자별 멱등성 마커
    private static final String IDEMPOTENCY_KEY_FORMAT = "post:%s:comment-path:%s:member-uuid:%s";
    // post:{ulid}:comment-path:{reservedPath} - 예약된 경로 (member-uuid 미포함)
    private static final String RESERVED_KEY_FORMAT = "post:%s:comment-path:%s";
    // post:{ulid}:comment-path: - 키 접두사 (형제 스캔 및 파싱용)
    private static final String KEY_PREFIX_FORMAT = "post:%s:comment-path:";

    private static final Duration TTL = Duration.ofHours(1);

    @Override
    public Optional<CommentPath> reservePath(PostId postId, CommentPath path, Author author,
                                             IntSupplier getMaximumSiblingPathOrderFromDB) {
        String postIdValue = postId.getValue();
        String pathValue = path.getValue();

        String idempotencyKey = IDEMPOTENCY_KEY_FORMAT.formatted(postIdValue, pathValue, author.getUuid());
        Boolean reserveSucceeded = stringRedisTemplate.opsForValue().setIfAbsent(idempotencyKey, "", TTL);
        if (reserveSucceeded == null) {
            throw new ConnectionFailedException(CONNECTION_FAILED, "redis");
        } else if (reserveSucceeded.equals(Boolean.FALSE)) {
            return Optional.empty();
        }

        String parentPath = getParentPath(pathValue);
        // 예약 키가 하나라도 살아 있으면 Redis 스캔값이 최신이므로 그대로 쓰고,
        // 전혀 없을 때(조용한 게시글 / TTL 만료 / 캐시 플러시 등)만 DB 하한값을 조회한다.
        int redisMaximumSiblingPathOrder = getMaximumSiblingPathOrder(postIdValue, parentPath);
        int nextPathOrder = (redisMaximumSiblingPathOrder > 0 ? redisMaximumSiblingPathOrder : getMaximumSiblingPathOrderFromDB.getAsInt()) + 1;
        String reservedPath = parentPath.isEmpty() ? String.valueOf(nextPathOrder) : parentPath + "." + nextPathOrder;
        stringRedisTemplate.opsForValue().set(RESERVED_KEY_FORMAT.formatted(postIdValue, reservedPath), "", TTL);

        return Optional.of(CommentPath.create(reservedPath));
    }

    private String getParentPath(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        return lastDotIndex < 0 ? "" : path.substring(0, lastDotIndex);
    }

    private int getMaximumSiblingPathOrder(String ulid, String parentPath) {
        String prefix = KEY_PREFIX_FORMAT.formatted(ulid);
        String siblingPathOrderGlob = parentPath.isEmpty() ? prefix + "*" : prefix + parentPath + ".*";

        Set<String> keys = stringRedisTemplate.keys(siblingPathOrderGlob);
        if (keys.isEmpty()) {
            return 0; // 댓글이 1 기반이므로 0은 키가 비었음을 의미
        }

        int maximumPathOrder = 0;
        for (String key : keys) {
            String pathWithOptionalMemberUuid = key.substring(prefix.length());
            if (pathWithOptionalMemberUuid.contains(":")) { // :member-uuid: 멱등성 키는 제외
                continue;
            }
            if (parentPath.isEmpty()) {
                if (pathWithOptionalMemberUuid.contains(".")) { // Depth 2 이상의 키는 제외
                    continue;
                }
            }

            String lastSegment = pathWithOptionalMemberUuid.substring(pathWithOptionalMemberUuid.lastIndexOf('.') + 1);
            maximumPathOrder = Math.max(maximumPathOrder, Integer.parseInt(lastSegment));
        }
        return maximumPathOrder;
    }
}
