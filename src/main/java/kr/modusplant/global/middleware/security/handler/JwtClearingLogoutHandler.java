package kr.modusplant.global.middleware.security.handler;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.modules.jwt.app.service.RefreshTokenApplicationService;
import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtClearingLogoutHandler implements LogoutHandler {

    private final TokenApplicationService tokenApplicationService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String refreshTokenFromClient = request.getHeader("Cookie");
        tokenApplicationService.removeToken(refreshTokenFromClient);
    }
}
