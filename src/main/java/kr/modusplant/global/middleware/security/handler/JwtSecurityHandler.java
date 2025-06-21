package kr.modusplant.global.middleware.security.handler;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import kr.modusplant.modules.jwt.app.dto.TokenPair;
import kr.modusplant.modules.jwt.app.service.RefreshTokenApplicationService;
import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
import kr.modusplant.modules.jwt.app.service.TokenProvider;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtSecurityHandler {

    @Value("${jwt.refresh_duration}")
    private long refreshDuration;

    private final TokenApplicationService tokenApplicationService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenApplicationService refreshTokenApplicationService;

    public Map<String, String> provideHeaderTokenPair(HttpServletRequest request) {
        String refreshToken = request.getHeader("Cookie").substring(7);
        TokenPair reissuedTokenPair = tokenApplicationService.reissueToken(refreshToken);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshDuration)
                .sameSite("Lax")
                .build();

        return Map.of(
                "accessToken", "Bearer " + reissuedTokenPair.accessToken(),
                "refreshToken", refreshTokenCookie.toString()
        );

    }

    public RefreshToken insertRefreshToken(String refreshToken) {
        Claims tokenClaims = tokenProvider.getClaimsFromToken(refreshToken);

        RefreshToken tokenToSave = RefreshToken.builder()
                .memberUuid(UUID.fromString(tokenClaims.getSubject()))
                .refreshToken(refreshToken)
                .issuedAt(tokenClaims.getIssuedAt())
                .expiredAt(tokenClaims.getExpiration())
                .build();

        return refreshTokenApplicationService.insert(tokenToSave);
    }
}
