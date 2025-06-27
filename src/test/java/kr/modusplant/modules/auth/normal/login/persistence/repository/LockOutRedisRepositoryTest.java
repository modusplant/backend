package kr.modusplant.modules.auth.normal.login.persistence.repository;

import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@RepositoryOnlyContext
class LockOutRedisRepositoryTest {
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private LockOutRedisRepository lockOutRedisRepository;

    private static final String EMAIL = "test@example.com";

    @Test
    @DisplayName("Redis failedAttempt값을 찾기")
    void getFailedAttemptsIfNotExistTest() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn(null);

        // when
        int result = lockOutRedisRepository.getFailedAttempts(EMAIL);

        // then
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("Redis failedAttempt값을 찾기")
    void getFailedAttemptsWithResultTest() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(anyString())).willReturn("2");

        // when
        int result = lockOutRedisRepository.getFailedAttempts(EMAIL);

        // then
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("Redis failedAttempt값을 증가시키기")
    void increaseFailedAttemptTest() {
        // given
        Duration ttl = Duration.ofMinutes(10);
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(anyString())).willReturn(2L);

        // when
        int result = lockOutRedisRepository.increaseFailedAttempt(EMAIL,ttl);

        // then
        assertThat(result).isEqualTo(2);
        then(stringRedisTemplate).should(never()).expire(anyString(),eq(ttl));
    }

    @Test
    @DisplayName("Redis에 failedAttempt가 없을 때 값을 증가하기")
    void increaseFailedAttemptNotExistKeyTest() {
        // given
        Duration ttl = Duration.ofMinutes(10);
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(anyString())).willReturn(1L);

        // when
        int result = lockOutRedisRepository.increaseFailedAttempt(EMAIL,ttl);

        // then
        assertThat(result).isEqualTo(1);
        then(stringRedisTemplate).should().expire(anyString(),eq(ttl));
    }

    @Test
    @DisplayName("failedAttempt 제거")
    void removeFailedAttemptTest() {
        // when
        lockOutRedisRepository.removeFailedAttempt(EMAIL);

        // then
        verify(stringRedisTemplate).delete(anyString());
    }

}