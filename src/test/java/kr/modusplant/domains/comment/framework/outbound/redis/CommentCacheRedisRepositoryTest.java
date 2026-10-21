package kr.modusplant.domains.comment.framework.outbound.redis;

import kr.modusplant.domains.comment.common.util.domain.vo.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.PostIdTestUtils;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.shared.exception.ConnectionFailedException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class CommentCacheRedisRepositoryTest implements PostIdTestUtils, CommentPathTestUtils, AuthorTestUtils {
    private final StringRedisTemplate stringRedisTemplate = Mockito.mock(StringRedisTemplate.class);

    @SuppressWarnings("unchecked")
    private final ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);

    private final CommentCacheRedisRepository repository = new CommentCacheRedisRepository(stringRedisTemplate);

    private static final Duration TTL = Duration.ofHours(1);
    private static final String IDEMPOTENCY_SCAN_PATTERN = "comment:idempotency:*";
    private static final String SIBLING_ORDER_SCAN_PATTERN = "comment:sibling-order:*";
    private static final int SCAN_BATCH_SIZE = 1000;
    private static final IntSupplier FAILING_DATABASE_LOOKUP = () -> {
        throw new AssertionError("database lower-bound lookup must not run when the counter is warm");
    };

    private String idempotencyKey(String pathValue) {
        return "comment:idempotency:post:" + TEST_POST_ULID + ":comment-path:" + pathValue
                + ":member-uuid:" + MEMBER_BASIC_USER_UUID;
    }

    private String counterKey(String parentIdentifier) {
        return "comment:sibling-order:post:" + TEST_POST_ULID + ":parent:" + parentIdentifier;
    }

    @Test
    @DisplayName("차가운 카운터로 새 요청 예약 시 DB 하한값에서 발급된 CommentPath 반환")
    void testReservePath_givenNewRequestWithColdCounter_willReturnCommentPath() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(true);
        given(stringRedisTemplate.hasKey(counterKey("ROOT"))).willReturn(false);
        given(valueOperations.increment(counterKey("ROOT"))).willReturn(5L);

        // when
        Optional<CommentPath> result = repository.reservePath(
                testPostId, testCommentRootPath, testAuthorWithUuid, () -> 4);

        // then
        assertThat(result).contains(CommentPath.create("5"));
        then(valueOperations).should(times(1)).setIfAbsent(idempotencyKey("1"), "", TTL);
        then(valueOperations).should(times(1)).setIfAbsent(counterKey("ROOT"), "4", TTL);
        then(valueOperations).should(times(1)).increment(counterKey("ROOT"));
        then(stringRedisTemplate).should(times(1)).expire(counterKey("ROOT"), TTL);
    }

    @Test
    @DisplayName("이미 처리된 요청이면 빈 Optional 반환")
    void testReservePath_givenDuplicateRequest_willReturnEmptyOptional() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(eq(idempotencyKey("1")), eq(""), eq(TTL))).willReturn(false);

        // when
        Optional<CommentPath> result = repository.reservePath(
                testPostId, testCommentRootPath, testAuthorWithUuid, FAILING_DATABASE_LOOKUP);

        // then
        assertThat(result).isEmpty();
        then(stringRedisTemplate).should(never()).hasKey(anyString());
        then(valueOperations).should(never()).increment(anyString());
    }

    @Test
    @DisplayName("살아 있는 카운터로 예약 시 DB 조회 없이 CommentPath 반환")
    void testReservePath_givenWarmSiblingCounter_willReturnCommentPath() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(true);
        given(stringRedisTemplate.hasKey(counterKey("ROOT"))).willReturn(true);
        given(valueOperations.increment(counterKey("ROOT"))).willReturn(3L);

        // when
        Optional<CommentPath> result = repository.reservePath(
                testPostId, testCommentRootPath, testAuthorWithUuid, FAILING_DATABASE_LOOKUP);

        // then
        assertThat(result).contains(CommentPath.create("3"));
        then(valueOperations).should(never()).setIfAbsent(eq(counterKey("ROOT")), anyString(), any(Duration.class));
        then(stringRedisTemplate).should(times(1)).expire(counterKey("ROOT"), TTL);
    }

    @Test
    @DisplayName("중첩 경로 예약 시 부모 경로가 접두어로 붙은 CommentPath 반환")
    void testReservePath_givenNestedPath_willReturnParentPrefixedCommentPath() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(true);
        given(stringRedisTemplate.hasKey(counterKey("1"))).willReturn(false);
        given(valueOperations.increment(counterKey("1"))).willReturn(4L);

        // when
        Optional<CommentPath> result = repository.reservePath(
                testPostId, testCommentPath, testAuthorWithUuid, () -> 3);

        // then
        assertThat(result).contains(CommentPath.create("1.4"));
        then(valueOperations).should(times(1)).setIfAbsent(idempotencyKey("1.2"), "", TTL);
        then(valueOperations).should(times(1)).setIfAbsent(counterKey("1"), "3", TTL);
    }

    @Test
    @DisplayName("Redis가 null을 응답하면 예외 반환")
    void testReservePath_givenNullReplyFromRedis_willThrowException() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(null);

        // when
        ConnectionFailedException ex = assertThrows(ConnectionFailedException.class,
                () -> repository.reservePath(testPostId, testCommentRootPath, testAuthorWithUuid, () -> 0));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(GeneralErrorCode.CONNECTION_FAILED);
    }

    @Test
    @DisplayName("Redis 데이터 접근 오류 발생 시 예외 반환")
    void testReservePath_givenDataAccessError_willThrowException() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(true);
        given(stringRedisTemplate.hasKey(counterKey("ROOT"))).willReturn(false);
        given(valueOperations.increment(counterKey("ROOT"))).willThrow(new QueryTimeoutException("redis down"));

        // when
        ConnectionFailedException ex = assertThrows(ConnectionFailedException.class,
                () -> repository.reservePath(testPostId, testCommentRootPath, testAuthorWithUuid, () -> 1));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(GeneralErrorCode.CONNECTION_FAILED);
    }

    @Test
    @DisplayName("두 키 계열에 캐시가 있으면 모두 만료시키고 만료된 키 개수 long 반환")
    void testEvictAllPathCaches_givenKeysInBothFamilies_willReturnLong() {
        // given
        Cursor<String> idempotencyCursor = cursorOf(List.of(idempotencyKey("1"), idempotencyKey("1.2")));
        Cursor<String> siblingOrderCursor = cursorOf(List.of(counterKey("ROOT")));
        given(stringRedisTemplate.scan(argThat(scanPattern(IDEMPOTENCY_SCAN_PATTERN)))).willReturn(idempotencyCursor);
        given(stringRedisTemplate.scan(argThat(scanPattern(SIBLING_ORDER_SCAN_PATTERN)))).willReturn(siblingOrderCursor);
        List<List<String>> deletedBatches = recordDeletedBatches();

        // when
        long result = repository.evictAllPathCaches();

        // then
        assertThat(result).isEqualTo(3L);
        assertThat(deletedBatches).containsExactly(
                List.of(idempotencyKey("1"), idempotencyKey("1.2")),
                List.of(counterKey("ROOT")));
        then(idempotencyCursor).should(times(1)).close();
        then(siblingOrderCursor).should(times(1)).close();
    }

    @Test
    @DisplayName("일괄 크기를 넘는 키가 있으면 일괄 크기 단위로 나누어 만료시키고 만료된 키 개수 long 반환")
    void testEvictAllPathCaches_givenMoreKeysThanBatchSize_willReturnLong() {
        // given
        List<String> keys = IntStream.rangeClosed(1, SCAN_BATCH_SIZE + 1)
                .mapToObj(order -> idempotencyKey(String.valueOf(order)))
                .toList();
        Cursor<String> idempotencyCursor = cursorOf(keys);
        Cursor<String> siblingOrderCursor = cursorOf(List.of());
        given(stringRedisTemplate.scan(argThat(scanPattern(IDEMPOTENCY_SCAN_PATTERN)))).willReturn(idempotencyCursor);
        given(stringRedisTemplate.scan(argThat(scanPattern(SIBLING_ORDER_SCAN_PATTERN)))).willReturn(siblingOrderCursor);
        List<List<String>> deletedBatches = recordDeletedBatches();

        // when
        long result = repository.evictAllPathCaches();

        // then
        assertThat(result).isEqualTo(SCAN_BATCH_SIZE + 1L);
        assertThat(deletedBatches).containsExactly(keys.subList(0, SCAN_BATCH_SIZE), keys.subList(SCAN_BATCH_SIZE, keys.size()));
    }

    @Test
    @DisplayName("일치하는 키가 없으면 삭제 없이 0을 long으로 반환")
    void testEvictAllPathCaches_givenNoMatchingKeys_willReturnLong() {
        // given
        Cursor<String> idempotencyCursor = cursorOf(List.of());
        Cursor<String> siblingOrderCursor = cursorOf(List.of());
        given(stringRedisTemplate.scan(argThat(scanPattern(IDEMPOTENCY_SCAN_PATTERN)))).willReturn(idempotencyCursor);
        given(stringRedisTemplate.scan(argThat(scanPattern(SIBLING_ORDER_SCAN_PATTERN)))).willReturn(siblingOrderCursor);

        // when
        long result = repository.evictAllPathCaches();

        // then
        assertThat(result).isZero();
        then(stringRedisTemplate).should(never()).delete(anyCollection());
    }

    @Test
    @DisplayName("키 탐색 중 Redis 데이터 접근 오류 발생 시 예외 반환")
    void testEvictAllPathCaches_givenDataAccessError_willThrowException() {
        // given
        given(stringRedisTemplate.scan(any(ScanOptions.class))).willThrow(new QueryTimeoutException("redis down"));

        // when
        ConnectionFailedException ex = assertThrows(ConnectionFailedException.class,
                repository::evictAllPathCaches);

        // then
        assertThat(ex.getErrorCode()).isEqualTo(GeneralErrorCode.CONNECTION_FAILED);
    }

    @SuppressWarnings("unchecked")
    private Cursor<String> cursorOf(List<String> keys) {
        Iterator<String> iterator = keys.iterator();
        Cursor<String> cursor = Mockito.mock(Cursor.class);
        given(cursor.hasNext()).willAnswer(invocation -> iterator.hasNext());
        given(cursor.next()).willAnswer(invocation -> iterator.next());
        return cursor;
    }

    private ArgumentMatcher<ScanOptions> scanPattern(String pattern) {
        return scanOptions -> scanOptions != null && pattern.equals(scanOptions.getPattern());
    }

    // 삭제 후 원본 목록이 비워지므로, 호출 시점의 키 목록을 복사하여 기록
    private List<List<String>> recordDeletedBatches() {
        List<List<String>> deletedBatches = new ArrayList<>();
        given(stringRedisTemplate.delete(anyCollection())).willAnswer(invocation -> {
            Collection<String> batch = invocation.getArgument(0);
            deletedBatches.add(List.copyOf(batch));
            return (long) batch.size();
        });
        return deletedBatches;
    }
}
