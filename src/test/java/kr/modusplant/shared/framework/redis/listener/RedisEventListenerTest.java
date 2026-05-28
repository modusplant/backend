package kr.modusplant.shared.framework.redis.listener;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import static kr.modusplant.domains.member.common.util.domain.event.RecentlyViewPostRemoveEventTestUtils.testRecentlyViewPostRemoveEvent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RedisEventListenerTest {
    private final StringRedisTemplate stringRedisTemplate = Mockito.mock(StringRedisTemplate.class);
    private final RedisEventListener redisEventListener = new RedisEventListener(stringRedisTemplate);

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("최근에 본 게시글 제거 이벤트 수신 시 Redis 키를 제거한다")
    void handleRecentlyViewPostRemove_shouldRemoveRedisKeys() {
        // given
        given(stringRedisTemplate.execute(any(RedisCallback.class))).willReturn(true);

        // when
        redisEventListener.handleRecentlyViewPostRemove(testRecentlyViewPostRemoveEvent);

        // then
        verify(stringRedisTemplate, times(1)).execute(any(RedisCallback.class));
    }
}