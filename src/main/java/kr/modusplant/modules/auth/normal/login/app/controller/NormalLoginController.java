package kr.modusplant.modules.auth.normal.login.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.modusplant.global.app.servlet.response.DataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class NormalLoginController {

    @Value("${jwt.refresh_duration}")
    private long refreshDuration;

    @PostMapping("/login-success")
    public ResponseEntity<DataResponse<Map<String, Object>>> sendLoginSuccess(
            @RequestAttribute("accessToken") String accessToken,
            @RequestAttribute("refreshToken") String refreshToken,
            @RequestAttribute("accessTokenExpirationTime") long accessTokenExpirationTime,
            @RequestAttribute("refreshTokenExpirationTime") long refreshTokenExpirationTime
    ) {

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshDuration)
                .sameSite("Lax")
                .build();

        Map<String, Object> accessTokenData = Map.of(
                "accessToken", accessToken,
                "accessTokenExpirationTime", accessTokenExpirationTime,
                "refreshTokenExpirationTime", refreshTokenExpirationTime);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(DataResponse.ok(accessTokenData));
    }

    @PostMapping("/login-fail")
    public ResponseEntity sendLoginFailure(HttpServletRequest request) {
        System.out.println("Arrived at the failure controller method.");
        AuthenticationException authException = (AuthenticationException) request.getAttribute("exception");
        System.out.println("The error message: " + authException.getMessage());

        // 메시지, status를 어떻게 줄까?
        return ResponseEntity.badRequest().build();
    }
}
