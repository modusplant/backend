package kr.modusplant.modules.jwt.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.global.app.servlet.response.DataResponse;
import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
import kr.modusplant.modules.jwt.app.dto.TokenPair;
import kr.modusplant.modules.jwt.app.http.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kr.modusplant.global.vo.SnakeCaseWord.SNAKE_REFRESH_TOKEN;


@Tag(name="Token API", description = "JWT API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TokenController {
    private final TokenApplicationService tokenApplicationService;

    // 토큰 갱신
    @Operation(summary = "ACCESS TOKEN 토큰 갱신 API", description = "REFRESH TOKEN을 받아 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succeeded : JWT 발급 완료")
    })
    @PostMapping("/auth/token/refresh")
    public ResponseEntity<DataResponse<?>> refreshToken(@CookieValue(SNAKE_REFRESH_TOKEN) String refreshToken) {

        TokenPair tokenPair = tokenApplicationService.reissueToken(refreshToken);

        DataResponse<TokenResponse> response = DataResponse.ok(new TokenResponse(tokenPair.getAccessToken()));
        String refreshCookie = setRefreshTokenCookie(tokenPair.getRefreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie).body(response);
    }

    private String setRefreshTokenCookie(String refreshToken) {
        ResponseCookie refreshCookie = ResponseCookie.from(SNAKE_REFRESH_TOKEN, refreshToken).build();
        return refreshCookie.toString();
    }
}