package kr.modusplant.infrastructure.jwt.framework.out.redis;

import kr.modusplant.framework.out.redis.RedisHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccessTokenRedisRepositoryTest {
    @Mock
    private RedisHelper redisHelper;

    @InjectMocks
    private AccessTokenRedisRepository accessTokenRedisRepository;

    private static final String TOKEN = "test-access-token";
    private static final long TTL_SECONDS = 3600L;

    @Test
    @DisplayName("블랙리스트에 access token 추가")
    void testAddToBlacklist_givenTokenAndTtl_willSetString() {
        // given
        willDoNothing().given(redisHelper).setString(anyString(), eq(TOKEN), any(Duration.class));

        // when
        accessTokenRedisRepository.addToBlacklist(TOKEN, TTL_SECONDS);

        // then
        then(redisHelper).should().setString(anyString(), eq(TOKEN), any(Duration.class));
        verify(redisHelper).setString(anyString(), eq(TOKEN), any(Duration.class));
    }

    @Test
    @DisplayName("블랙리스트에 있는 토큰인지 확인 - 존재하는 경우")
    void testIsBlacklisted_givenToken_willReturnTrue() {
        // given
        given(redisHelper.exists(anyString())).willReturn(true);

        // when
        boolean result = accessTokenRedisRepository.isBlacklisted(TOKEN);

        // then
        assertThat(result).isEqualTo(true);
        then(redisHelper).should().exists(anyString());
        verify(redisHelper).exists(anyString());
    }

    @Test
    @DisplayName("블랙리스트에 있는 토큰인지 확인 - 존재하지 않는 경우")
    void testIsBlacklisted_givenToken_willReturnFalse() {
        // given
        given(redisHelper.exists(anyString())).willReturn(false);

        // when
        boolean result = accessTokenRedisRepository.isBlacklisted(TOKEN);

        // then
        assertThat(result).isEqualTo(false);
        then(redisHelper).should().exists(anyString());
        verify(redisHelper).exists(anyString());
    }

    @Test
    @DisplayName("블랙리스트에서 토큰 제거")
    void testRemoveFromBlacklist_givenToken_willDelete() {
        // given
        willDoNothing().given(redisHelper).delete(anyString());

        // when
        accessTokenRedisRepository.removeFromBlacklist(TOKEN);

        // then
        then(redisHelper).should().delete(anyString());
        verify(redisHelper).delete(anyString());
    }
}