package kr.modusplant.shared.framework.redis.listener;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.util.domain.event.MemberSearchHistoryRemoveEventTestUtils.testMemberSearchHistoryRemoveEvent;
import static kr.modusplant.domains.member.common.util.domain.event.RecentlyViewMemberRemoveEventTestUtils.testRecentlyViewMemberRemoveEvent;
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

    @Test
    @DisplayName("회원의 최근 본 게시글 제거 이벤트로 Redis 키 제거 활동 수행")
    void testHandleRecentlyViewMemberRemove_givenRemoveEvent_willRemoveRedisKey() {
        // given
        given(stringRedisTemplate.unlink(any(String.class))).willReturn(true);

        // when
        redisEventListener.handleRecentlyViewMemberRemove(testRecentlyViewMemberRemoveEvent);

        // then
        verify(stringRedisTemplate, times(1)).unlink("recentlyView:member:%s:posts".formatted(MEMBER_BASIC_USER_UUID));
    }

    @Test
    @DisplayName("회원의 검색 기록 제거 이벤트로 Redis 키 제거 활동 수행")
    void testHandleMemberSearchHistoryRemove_givenRemoveEvent_willRemoveRedisKey() {
        // given
        given(stringRedisTemplate.unlink(any(String.class))).willReturn(true);

        // when
        redisEventListener.handleMemberSearchHistoryRemove(testMemberSearchHistoryRemoveEvent);

        // then
        verify(stringRedisTemplate, times(1)).unlink("searchHistory:member:%s".formatted(MEMBER_BASIC_USER_UUID));
    }
}