package kr.modusplant.modules.jwt.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.modules.jwt.app.dto.TokenPair;
import kr.modusplant.modules.jwt.app.http.response.TokenResponse;
import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Token API", description = "JWT API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TokenController {

    @Value("${jwt.refresh_duration}")
    private long refreshDuration;

    private final TokenApplicationService tokenService;

    @Operation(summary = "JWT 토큰 갱신 API", description = "리프레시 토큰을 받아 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succeeded : JWT 발급 완료")
    })
    @PostMapping("/auth/token/refresh")
    public ResponseEntity<DataResponse<?>> refreshToken(@CookieValue("Cookie") String refreshToken) {

        TokenPair tokenPair = tokenService.reissueToken(refreshToken);

        TokenResponse tokenResponse = new TokenResponse(tokenPair.accessToken());
        DataResponse<TokenResponse> response = DataResponse.ok(tokenResponse);
        String refreshCookie = setRefreshTokenCookie(tokenPair.refreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie).body(response);
    }

    private String setRefreshTokenCookie(String refreshToken) {
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshDuration)
                .sameSite("Lax")
                .build();
        return refreshCookie.toString();
    }
}