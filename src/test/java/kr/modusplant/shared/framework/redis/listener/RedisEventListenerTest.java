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
    @DisplayName("최근 본 게시글 제거 이벤트로 Redis 키 제거 활동 수행")
    void testHandleRecentlyViewPostRemove_givenRemoveEvent_willRemoveRedisKeys() {
        // given
        given(stringRedisTemplate.execute(any(RedisCallback.class))).willReturn(true);

        // when
        redisEventListener.handleRecentlyViewPostRemove(testRecentlyViewPostRemoveEvent);

        // then
        verify(stringRedisTemplate, times(1)).execute(any(RedisCallback.class));
    }
}