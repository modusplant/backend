package kr.modusplant.modules.auth.normal.login.app.controller;

import kr.modusplant.global.app.http.response.DataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class NormalLoginController {

    @Value("${jwt.refresh_duration}")
    private long refreshDuration;

    @PostMapping("/login-success")
    public ResponseEntity<DataResponse<Map<String, Object>>> sendLoginSuccess(
            @RequestAttribute("accessToken") String accessToken,
            @RequestAttribute("refreshToken") String refreshToken
    ) {

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshDuration)
                .sameSite("Lax")
                .build();

        Map<String, Object> accessTokenData = Map.of("accessToken", accessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(DataResponse.ok(accessTokenData));
    }

    @PostMapping("/login-fail")
    public ResponseEntity<DataResponse<Void>> sendLoginFailure(
            @RequestAttribute("errorMessage") String errorMessage
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(DataResponse.of(HttpStatus.UNAUTHORIZED.value(), errorMessage));
    }
}
