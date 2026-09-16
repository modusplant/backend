package kr.modusplant.infrastructure.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.BadCredentialException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class JwtClearingLogoutHandlerTest {

    private final TokenService tokenService = Mockito.mock(TokenService.class);
    private final JwtClearingLogoutHandler handler = new JwtClearingLogoutHandler(tokenService);

    @Test
    @DisplayName("refresh token과 access token으로 토큰 제거 및 블랙리스트 등록 활동 수행")
    void testLogout_givenRefreshAndAccessToken_willRemoveAndBlacklistToken() {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(request.getCookies()).willReturn(new Cookie[]{new Cookie("refreshToken", "refresh-value")});
        given(request.getHeader("Authorization")).willReturn("Bearer access-value");

        // when
        handler.logout(request, response, authentication);

        // then
        then(tokenService).should().removeToken("refresh-value");
        then(tokenService).should().blacklistAccessToken("access-value");
    }

    @Test
    @DisplayName("refresh token 쿠키가 없어 예외 반환")
    void testLogout_givenNoRefreshTokenCookie_willThrowException() {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(request.getCookies()).willReturn(null);
        given(request.getHeader("Authorization")).willReturn("Bearer access-value");

        // when & then
        assertThatThrownBy(() -> handler.logout(request, response, authentication))
                .isInstanceOf(BadCredentialException.class)
                .extracting("errorCode").isEqualTo(SecurityErrorCode.BAD_CREDENTIALS);
    }

    @Test
    @DisplayName("Authorization 헤더가 없어 예외 반환")
    void testLogout_givenNoAuthorizationHeader_willThrowException() {
        // given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(request.getCookies()).willReturn(new Cookie[]{new Cookie("refreshToken", "refresh-value")});
        given(request.getHeader("Authorization")).willReturn(null);

        // when & then
        assertThatThrownBy(() -> handler.logout(request, response, authentication))
                .isInstanceOf(BadCredentialException.class)
                .extracting("errorCode").isEqualTo(SecurityErrorCode.BAD_CREDENTIALS);
    }
}
