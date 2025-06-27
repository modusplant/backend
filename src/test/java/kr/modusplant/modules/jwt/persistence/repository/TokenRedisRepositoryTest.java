package kr.modusplant.modules.jwt.persistence.repository;

import kr.modusplant.global.context.RepositoryOnlyContext;
import kr.modusplant.global.middleware.redis.RedisHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;

@RepositoryOnlyContext
class TokenRedisRepositoryTest {
    @Mock
    private RedisHelper redisHelper;

    @InjectMocks
    private TokenRedisRepository tokenRedisRepository;

    private static final String TOKEN = "test-access-token";
    private static final long TTL_SECONDS = 3600L;

    @Test
    @DisplayName("블랙리스트에 access token 추가")
    void addToBlacklistTest() {
        // given
        willDoNothing().given(redisHelper).setString(anyString(), eq(TOKEN), any(Duration.class));

        // when
        tokenRedisRepository.addToBlacklist(TOKEN, TTL_SECONDS);

        // then
        then(redisHelper).should().setString(anyString(), eq(TOKEN), any(Duration.class));
        verify(redisHelper).setString(anyString(), eq(TOKEN), any(Duration.class));
    }

    @Test
    @DisplayName("블랙리스트에 있는 토큰인지 확인 - 존재하는 경우")
    void isBlacklistedWhenTokenExistsTest() {
        // given
        given(redisHelper.exists(anyString())).willReturn(true);

        // when
        boolean result = tokenRedisRepository.isBlacklisted(TOKEN);

        // then
        assertThat(result).isEqualTo(true);
        then(redisHelper).should().exists(anyString());
        verify(redisHelper).exists(anyString());
    }

    @Test
    @DisplayName("블랙리스트에 있는 토큰인지 확인 - 존재하지 않는 경우")
    void isBlacklistedWhenTokenNotExistsTest() {
        // given
        given(redisHelper.exists(anyString())).willReturn(false);

        // when
        boolean result = tokenRedisRepository.isBlacklisted(TOKEN);

        // then
        assertThat(result).isEqualTo(false);
        then(redisHelper).should().exists(anyString());
        verify(redisHelper).exists(anyString());
    }

    @Test
    @DisplayName("블랙리스트에서 토큰 제거")
    void removeFromBlacklistTest() {
        // given
        willDoNothing().given(redisHelper).delete(anyString());

        // when
        tokenRedisRepository.removeFromBlacklist(TOKEN);

        // then
        then(redisHelper).should().delete(anyString());
        verify(redisHelper).delete(anyString());
    }
}