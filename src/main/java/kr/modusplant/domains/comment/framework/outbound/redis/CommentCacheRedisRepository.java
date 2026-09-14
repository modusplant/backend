package kr.modusplant.domains.comment.framework.outbound.redis;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.port.repository.CommentCacheRepository;
import kr.modusplant.shared.exception.ConnectionFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.function.IntSupplier;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.CONNECTION_FAILED;

@Repository
@RequiredArgsConstructor
public class CommentCacheRedisRepository implements CommentCacheRepository {
    private final StringRedisTemplate stringRedisTemplate;

    // comment:idempotency:post:{ulid}:comment-path:{path}:member-uuid:{uuid} - 작성자별 멱등성 마커(값은 빈 문자열)
    private static final String IDEMPOTENCY_KEY_FORMAT =
            "comment:idempotency:post:%s:comment-path:%s:member-uuid:%s";
    // comment:sibling-order:post:{ulid}:parent:{parentPath|ROOT} - 같은 부모 아래 마지막으로 발급된 형제 순서 카운터에 대한 키
    private static final String SIBLING_ORDER_KEY_FORMAT = "comment:sibling-order:post:%s:parent:%s";
    // parentPath가 빈 문자열(루트)일 때 카운터 키에 넣을 리터럴 식별자 (빈 식별자 방지)
    private static final String ROOT_PARENT_IDENTIFIER = "ROOT";

    private static final Duration TTL = Duration.ofHours(1);

    @Override
    public Optional<CommentPath> reservePath(PostId postId, CommentPath path, Author author,
                                             IntSupplier getMaximumSiblingPathOrderFromDB) {
        String postIdValue = postId.getValue();
        String pathValue = path.getValue();

        try {
            String idempotencyKey =
                    IDEMPOTENCY_KEY_FORMAT.formatted(postIdValue, pathValue, author.getUuid());
            Boolean reserveSucceeded = stringRedisTemplate.opsForValue().setIfAbsent(idempotencyKey, "", TTL);
            if (reserveSucceeded == null) {
                throw new ConnectionFailedException(CONNECTION_FAILED, "redis");
            }
            if (reserveSucceeded.equals(Boolean.FALSE)) {
                return Optional.empty(); // 이미 처리된 (게시글, 요청 경로, 작성자) 요청 - 멱등 보장을 위해 무시
            }

            String parentPath = getParentPath(pathValue);
            long nextPathOrder = issueNextSiblingPathOrder(postIdValue, parentPath, getMaximumSiblingPathOrderFromDB);
            String reservedPath = parentPath.isEmpty()
                    ? String.valueOf(nextPathOrder)
                    : parentPath + "." + nextPathOrder;

            return Optional.of(CommentPath.create(reservedPath));
        } catch (DataAccessException e) {
            throw new ConnectionFailedException(CONNECTION_FAILED, "redis", e);
        }
    }

    /**
     * 같은 부모 아래 다음 형제 경로 순서를 (게시글, 부모경로)당 단일 카운터 키에서 원자적으로 발급한다.
     * 카운터가 없을 때에만 DB 하한값으로 값을 설정하며, 그 외에는 {@code INCR} 한 번으로 끝난다.
     *
     * <p><b>Load-Bearing:</b> 값 설정({@code SET NX})은 반드시 {@code INCR} 보다 먼저 실행되어야 한다.
     * {@code INCR} 는 존재하지 않는 키를 0에서 자동 생성하므로, 값 설정 없이 {@code INCR} 가 먼저 돌면
     * DB 하한값 대신 1을 발급해 기존 형제 경로와 충돌한다.
     */
    private long issueNextSiblingPathOrder(String postIdValue, String parentPath,
                                           IntSupplier getMaximumSiblingPathOrderFromDB) {
        String counterKey = SIBLING_ORDER_KEY_FORMAT.formatted(
                postIdValue, parentPath.isEmpty() ? ROOT_PARENT_IDENTIFIER : parentPath);

        // 카운터가 살아 있는 활성 게시글에서는 DB 조회가 발생하지 않음
        if (!stringRedisTemplate.hasKey(counterKey)) {
            int maximumSiblingPathOrder = getMaximumSiblingPathOrderFromDB.getAsInt();
            stringRedisTemplate.opsForValue()
                    .setIfAbsent(counterKey, String.valueOf(maximumSiblingPathOrder), TTL);
        }

        Long nextPathOrder = stringRedisTemplate.opsForValue().increment(counterKey);
        if (nextPathOrder == null) {
            throw new ConnectionFailedException(CONNECTION_FAILED, "redis");
        }
        stringRedisTemplate.expire(counterKey, TTL); // 슬라이딩 TTL 갱신

        return nextPathOrder;
    }

    private String getParentPath(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        return lastDotIndex < 0 ? "" : path.substring(0, lastDotIndex);
    }
}
