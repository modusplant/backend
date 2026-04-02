package kr.modusplant.infrastructure.jwt.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import static kr.modusplant.infrastructure.jwt.constant.CookieName.REFRESH_TOKEN_COOKIE_NAME;


/**
 * JWT 쿠키 제공 Provider
 *
 * 기능 : JWT를 쿠키로 제공
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtCookieProvider {
    @Value("${jwt.refresh_duration}")
    private long refreshDuration;

    public String generateRefreshTokenCookieAsString(String refreshToken) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshDuration)
                .sameSite("Lax")
                .build();
        return refreshCookie.toString();
    }

}
