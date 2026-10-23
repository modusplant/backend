package kr.modusplant.infrastructure.jwt.listener;

import kr.modusplant.infrastructure.jwt.event.AccessTokenBlacklistEvent;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TokenEventListenerTest {
    private final TokenService tokenService = Mockito.mock(TokenService.class);
    private final TokenEventListener tokenEventListener = new TokenEventListener(tokenService);

    @Test
    @DisplayName("접근 토큰 블랙리스팅 이벤트로 handleAccessTokenBlacklist으로 활동 수행")
    void testHandleAccessTokenBlacklist_givenAccessToken_willBlacklistAccessToken() {
        // given
        AccessTokenBlacklistEvent event = AccessTokenBlacklistEvent.create(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);
        willDoNothing().given(tokenService).blacklistAccessToken(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);

        // when
        tokenEventListener.handleAccessTokenBlacklist(event);

        // then
        verify(tokenService, times(1)).blacklistAccessToken(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);
    }

    @Test
    @DisplayName("접근 토큰 블랙리스팅 실패 시 handleAccessTokenBlacklist으로 활동 수행")
    void testHandleAccessTokenBlacklist_givenBlacklistFailure_willLogError() {
        // given
        AccessTokenBlacklistEvent event = AccessTokenBlacklistEvent.create(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);
        willThrow(new RuntimeException()).given(tokenService).blacklistAccessToken(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);

        // when & then
        assertDoesNotThrow(() -> tokenEventListener.handleAccessTokenBlacklist(event));
        verify(tokenService, times(1)).blacklistAccessToken(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);
    }
}
