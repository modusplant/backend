package kr.modusplant.infrastructure.security.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.BadCredentialException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.util.WebUtils;

@RequiredArgsConstructor
public class JwtClearingLogoutHandler implements LogoutHandler {

    private final TokenService tokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie refreshToken = WebUtils.getCookie(request, "refreshToken");
        String accessToken = request.getHeader("Authorization");

        if (refreshToken == null) { throw new BadCredentialException(SecurityErrorCode.BAD_CREDENTIALS); }
        if (accessToken == null) { throw new BadCredentialException(SecurityErrorCode.BAD_CREDENTIALS); }

        tokenService.removeToken(refreshToken.getValue());
        tokenService.blacklistAccessToken(accessToken.substring(7));
    }
}
