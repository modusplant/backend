package kr.modusplant.global.middleware.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@RequiredArgsConstructor
public class JwtClearingLogoutHandler implements LogoutHandler {

    private final TokenApplicationService tokenApplicationService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String refreshTokenFromClient = request.getHeader("Cookie");
        tokenApplicationService.removeToken(refreshTokenFromClient);
    }
}
