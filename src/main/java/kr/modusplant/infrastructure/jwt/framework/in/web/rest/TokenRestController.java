package kr.modusplant.infrastructure.jwt.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.jwt.response.TokenResponse;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static kr.modusplant.infrastructure.jwt.util.TokenUtils.getTokenFromAuthorizationHeader;

@Tag(name="Token API", description = "JWT API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TokenRestController {

    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "JWT 토큰 갱신 API", description = "리프레시 토큰을 받아 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succeeded : JWT 발급 완료")
    })
    @PostMapping("/auth/token/refresh")
    public ResponseEntity<DataResponse<?>> refreshToken(@CookieValue("Cookie") String refreshToken,
                                                        @RequestHeader("Authorization") String rawAccessToken) {

        String accessToken = getTokenFromAuthorizationHeader(rawAccessToken);

        TokenPair tokenPair = tokenService.verifyAndReissueToken(accessToken, refreshToken);

        TokenResponse tokenResponse = new TokenResponse(tokenPair.accessToken());
        DataResponse<TokenResponse> response = DataResponse.ok(tokenResponse);
        String refreshCookie = jwtTokenProvider.generateRefreshTokenCookieAsString(tokenPair.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie)
                .cacheControl(CacheControl.noStore())
                .body(response);
    }
}