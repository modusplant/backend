package kr.modusplant.domains.member.framework.outbound.redis;

import kr.modusplant.domains.comment.common.util.framework.outbound.jpa.entity.CommentEntityTestUtils;
import kr.modusplant.domains.comment.framework.outbound.jpa.repository.CommentJpaRepository;
import kr.modusplant.shared.exception.ConnectionFailedException;
import kr.modusplant.shared.exception.ExistsValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectCommentIdTestUtils.testActivitySubjectCommentId;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EXISTS_CHOSEN_COMMENT;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

class ActivitySubjectPostCacheRedisRepositoryTest implements CommentEntityTestUtils {
    private final StringRedisTemplate stringRedisTemplate = Mockito.mock(StringRedisTemplate.class);

    @SuppressWarnings("unchecked")
    private final ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);

    private final CommentJpaRepository commentJpaRepository = Mockito.mock(CommentJpaRepository.class);

    private final ActivitySubjectPostCacheRedisRepository repository =
            new ActivitySubjectPostCacheRedisRepository(stringRedisTemplate, commentJpaRepository);

    private static final Duration TTL = Duration.ofHours(1);
    private static final String CHOSEN_COMMENT_KEY = "choose:post:" + TEST_POST_ULID + ":comment-path";
    private static final String OTHER_PATH = "1.9";

    @Test
    @DisplayName("캐시에 다른 경로가 있을 때 hasChosenCommentWithDifferentPath로 true 반환")
    void testHasChosenCommentWithDifferentPath_givenCachedDifferentPath_willReturnTrue() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndExpire(CHOSEN_COMMENT_KEY, TTL)).willReturn(OTHER_PATH);

        // when
        boolean result = repository.hasChosenCommentWithDifferentPath(testActivitySubjectCommentId);

        // then
        assertThat(result).isTrue();
        then(commentJpaRepository).should(never()).findFirstByPostUlidAndIsChosenTrue(any());
    }

    @Test
    @DisplayName("캐시에 같은 경로가 있을 때 hasChosenCommentWithDifferentPath로 false 반환")
    void testHasChosenCommentWithDifferentPath_givenCachedSamePath_willReturnFalse() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndExpire(CHOSEN_COMMENT_KEY, TTL)).willReturn(TEST_COMMENT_PATH);

        // when
        boolean result = repository.hasChosenCommentWithDifferentPath(testActivitySubjectCommentId);

        // then
        assertThat(result).isFalse();
        then(commentJpaRepository).should(never()).findFirstByPostUlidAndIsChosenTrue(any());
    }

    @Test
    @DisplayName("캐시가 비고 DB에 다른 경로가 있을 때 캐시 등록 후 true 반환")
    void testHasChosenCommentWithDifferentPath_givenEmptyCacheAndChosenInDatabase_willReturnTrue() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndExpire(CHOSEN_COMMENT_KEY, TTL)).willReturn(null);
        given(commentJpaRepository.findFirstByPostUlidAndIsChosenTrue(TEST_POST_ULID))
                .willReturn(Optional.of(createCommentEntityBuilder().path(OTHER_PATH).build()));

        // when
        boolean result = repository.hasChosenCommentWithDifferentPath(testActivitySubjectCommentId);

        // then
        assertThat(result).isTrue();
        then(valueOperations).should(times(1)).set(CHOSEN_COMMENT_KEY, OTHER_PATH, TTL);
    }

    @Test
    @DisplayName("캐시가 비고 DB에 같은 경로가 있을 때 캐시 등록 후 false 반환")
    void testHasChosenCommentWithDifferentPath_givenEmptyCacheAndSamePathInDatabase_willReturnFalse() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndExpire(CHOSEN_COMMENT_KEY, TTL)).willReturn(null);
        given(commentJpaRepository.findFirstByPostUlidAndIsChosenTrue(TEST_POST_ULID))
                .willReturn(Optional.of(createCommentEntityBuilder().path(TEST_COMMENT_PATH).build()));

        // when
        boolean result = repository.hasChosenCommentWithDifferentPath(testActivitySubjectCommentId);

        // then
        assertThat(result).isFalse();
        then(valueOperations).should(times(1)).set(CHOSEN_COMMENT_KEY, TEST_COMMENT_PATH, TTL);
    }

    @Test
    @DisplayName("캐시와 DB 모두 비었을 때 hasChosenCommentWithDifferentPath로 false 반환")
    void testHasChosenCommentWithDifferentPath_givenNoChosenComment_willReturnFalse() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndExpire(CHOSEN_COMMENT_KEY, TTL)).willReturn(null);
        given(commentJpaRepository.findFirstByPostUlidAndIsChosenTrue(TEST_POST_ULID)).willReturn(Optional.empty());

        // when
        boolean result = repository.hasChosenCommentWithDifferentPath(testActivitySubjectCommentId);

        // then
        assertThat(result).isFalse();
        then(valueOperations).should(never()).set(any(), any(), any(Duration.class));
    }

    @Test
    @DisplayName("Redis 오류로 인해 예외 반환")
    void testHasChosenCommentWithDifferentPath_givenRedisFailure_willThrowException() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndExpire(CHOSEN_COMMENT_KEY, TTL)).willThrow(new QueryTimeoutException("timeout"));

        // when
        ConnectionFailedException exception = assertThrows(ConnectionFailedException.class,
                () -> repository.hasChosenCommentWithDifferentPath(testActivitySubjectCommentId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.CONNECTION_FAILED);
    }

    @Test
    @DisplayName("캐시가 비어 있을 때 cacheBeforeChoose 실행 시 활동 수행")
    void testCacheBeforeChoose_givenEmptyCache_willProcessAction() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(CHOSEN_COMMENT_KEY, TEST_COMMENT_PATH, TTL)).willReturn(true);

        // when
        assertDoesNotThrow(() -> repository.cacheBeforeChoose(testActivitySubjectCommentId));

        // then
        then(valueOperations).should(never()).get(any());
    }

    @Test
    @DisplayName("같은 경로가 이미 선점되어 있을 때 cacheBeforeChoose 실행 시 활동 수행")
    void testCacheBeforeChoose_givenCachedSamePath_willProcessAction() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(CHOSEN_COMMENT_KEY, TEST_COMMENT_PATH, TTL)).willReturn(false);
        given(valueOperations.get(CHOSEN_COMMENT_KEY)).willReturn(TEST_COMMENT_PATH);

        // when
        assertDoesNotThrow(() -> repository.cacheBeforeChoose(testActivitySubjectCommentId));

        // then
        then(valueOperations).should(times(1)).get(CHOSEN_COMMENT_KEY);
    }

    @Test
    @DisplayName("다른 경로가 이미 선점되어 있을 때 cacheBeforeChoose 실행 시 예외 반환")
    void testCacheBeforeChoose_givenCachedDifferentPath_willThrowException() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(CHOSEN_COMMENT_KEY, TEST_COMMENT_PATH, TTL)).willReturn(false);
        given(valueOperations.get(CHOSEN_COMMENT_KEY)).willReturn(OTHER_PATH);

        // when
        ExistsValueException exception = assertThrows(ExistsValueException.class,
                () -> repository.cacheBeforeChoose(testActivitySubjectCommentId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(EXISTS_CHOSEN_COMMENT);
    }

    @Test
    @DisplayName("Redis 오류로 인해 cacheBeforeChoose 실행 시 예외 반환")
    void testCacheBeforeChoose_givenRedisFailure_willThrowException() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(CHOSEN_COMMENT_KEY, TEST_COMMENT_PATH, TTL))
                .willThrow(new QueryTimeoutException("timeout"));

        // when
        ConnectionFailedException exception = assertThrows(ConnectionFailedException.class,
                () -> repository.cacheBeforeChoose(testActivitySubjectCommentId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.CONNECTION_FAILED);
    }

    @Test
    @DisplayName("같은 경로가 캐시에 있을 때 removeChooseCache 실행 시 활동 수행")
    void testRemoveChooseCache_givenCachedSamePath_willProcessAction() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(CHOSEN_COMMENT_KEY)).willReturn(TEST_COMMENT_PATH);

        // when
        repository.removeChooseCache(testActivitySubjectCommentId);

        // then
        then(stringRedisTemplate).should(times(1)).delete(CHOSEN_COMMENT_KEY);
    }

    @Test
    @DisplayName("다른 경로가 캐시에 있을 때 removeChooseCache 실행 시 활동 수행")
    void testRemoveChooseCache_givenCachedDifferentPath_willProcessAction() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(CHOSEN_COMMENT_KEY)).willReturn(OTHER_PATH);

        // when
        repository.removeChooseCache(testActivitySubjectCommentId);

        // then
        then(stringRedisTemplate).should(never()).delete(any(String.class));
    }

    @Test
    @DisplayName("캐시가 비어 있을 때 removeChooseCache 실행 시 활동 수행")
    void testRemoveChooseCache_givenEmptyCache_willProcessAction() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(CHOSEN_COMMENT_KEY)).willReturn(null);

        // when
        repository.removeChooseCache(testActivitySubjectCommentId);

        // then
        then(stringRedisTemplate).should(never()).delete(any(String.class));
    }

    @Test
    @DisplayName("Redis 오류로 인해 removeChooseCache 실행 시 예외 반환")
    void testRemoveChooseCache_givenRedisFailure_willThrowException() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(CHOSEN_COMMENT_KEY)).willThrow(new QueryTimeoutException("timeout"));

        // when
        ConnectionFailedException exception = assertThrows(ConnectionFailedException.class,
                () -> repository.removeChooseCache(testActivitySubjectCommentId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.CONNECTION_FAILED);
    }
}
