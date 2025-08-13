package kr.modusplant.legacy.modules.auth.normal.login.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.framework.outbound.jackson.http.response.DataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "일반 로그인 API", description = "일반 로그인 도메인을 다루는 API입니다.")
@RestController
@RequestMapping("/api/auth")
@Validated
public class NormalLoginController {

    @Value("${jwt.refresh_duration}")
    private long refreshDuration;

    @Operation(
            summary = "일반 로그인 API",
            description = "토큰을 통해 일반 로그인 절차를 수행합니다."
    )
    @PostMapping("/login-success")
    public ResponseEntity<DataResponse<Map<String, Object>>> sendLoginSuccess(
            @Parameter(schema = @Schema(
                    description = "접근 토큰")
            )
            @RequestAttribute("accessToken")
            @NotBlank(message = "접근 토큰이 비어 있습니다.")
            String accessToken,

            @Parameter(schema = @Schema(
                    description = "리프레시 토큰")
            )
            @RequestAttribute("refreshToken")
            @NotBlank(message = "리프레시 토큰이 비어 있습니다.")
            String refreshToken) {

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
                .cacheControl(CacheControl.noStore())
                .body(DataResponse.ok(accessTokenData));
    }
}
