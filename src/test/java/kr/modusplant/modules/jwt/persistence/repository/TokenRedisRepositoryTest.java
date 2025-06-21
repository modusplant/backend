package kr.modusplant.modules.jwt.persistence.repository;

import kr.modusplant.global.context.RepositoryOnlyContext;
import kr.modusplant.global.middleware.redis.RedisHelper;
import kr.modusplant.global.common.util.HashUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

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
    private static final String HASHED_TOKEN = "hashed-test-token";
    private static final String REDIS_KEY = String.format("blacklist:access_token:%s", HASHED_TOKEN);

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = Mockito.mockStatic(HashUtils.class);
        when(HashUtils.sha256(TOKEN)).thenReturn(HASHED_TOKEN);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("블랙리스트에 access token 추가")
    void addToBlacklistTest() {
        // given
        willDoNothing().given(redisHelper).setString(eq(REDIS_KEY), eq(TOKEN), any(Duration.class));

        // when
        tokenRedisRepository.addToBlacklist(TOKEN, TTL_SECONDS);

        // then
        verify(redisHelper).setString(eq(REDIS_KEY), eq(TOKEN), any(Duration.class));
    }

    @Test
    @DisplayName("블랙리스트에 있는 토큰인지 확인 - 존재하는 경우")
    void isBlacklistedWhenTokenExistsTest() {
        // given
        given(redisHelper.exists(REDIS_KEY)).willReturn(true);

        // when
        boolean result = tokenRedisRepository.isBlacklisted(TOKEN);

        // then
        assertThat(result).isEqualTo(true);
        verify(redisHelper).exists(REDIS_KEY);
    }

    @Test
    @DisplayName("블랙리스트에 있는 토큰인지 확인 - 존재하지 않는 경우")
    void isBlacklistedWhenTokenNotExistsTest() {
        // given
        given(redisHelper.exists(REDIS_KEY)).willReturn(false);

        // when
        boolean result = tokenRedisRepository.isBlacklisted(TOKEN);

        // then
        assertThat(result).isEqualTo(false);
        verify(redisHelper).exists(REDIS_KEY);
    }

    @Test
    @DisplayName("블랙리스트에서 토큰 제거")
    void removeFromBlacklistTest() {
        // given
        willDoNothing().given(redisHelper).delete(REDIS_KEY);

        // when
        tokenRedisRepository.removeFromBlacklist(TOKEN);

        // then
        verify(redisHelper).delete(REDIS_KEY);
    }
}