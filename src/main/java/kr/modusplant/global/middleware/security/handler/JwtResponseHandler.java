package kr.modusplant.global.middleware.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import kr.modusplant.modules.jwt.app.dto.TokenPair;
import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtResponseHandler {

    @Value("${jwt.refresh_duration}")
    private long refreshDuration;

    private final TokenApplicationService tokenApplicationService;

    public Map<String, String> provideHeaders(HttpServletRequest request) {
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
                "Authorization", "Bearer " + reissuedTokenPair.accessToken(),
                "Set-Cookie", refreshTokenCookie.toString()
        );

    }
}
