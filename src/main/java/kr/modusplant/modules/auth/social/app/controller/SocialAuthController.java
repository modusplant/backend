package kr.modusplant.modules.auth.social.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.modusplant.modules.auth.social.app.dto.JwtUserPayload;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.global.app.servlet.response.DataResponse;
import kr.modusplant.modules.auth.social.app.http.request.SocialLoginRequest;
import kr.modusplant.modules.auth.social.app.service.SocialAuthApplicationService;
import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
import kr.modusplant.modules.jwt.app.dto.TokenPair;
import kr.modusplant.modules.jwt.app.http.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name="Social Login API", description = "소셜 로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SocialAuthController {

    private final SocialAuthApplicationService socialAuthApplicationService;
    private final TokenApplicationService tokenApplicationService;

    @Operation(summary = "카카오 소셜 로그인 API", description = "카카오 인가코드를 받아 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succeeded : Kakao Login"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid client input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or missing authentication credentials"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error: An unexpected error occurred on the Authorization server")
    })
    @PostMapping("/kakao/social-login")
    public ResponseEntity<DataResponse<?>> kakaoSocialLogin(@Valid @RequestBody SocialLoginRequest request) {
        JwtUserPayload member = socialAuthApplicationService.handleSocialLogin(AuthProvider.KAKAO, request.getCode());

        TokenPair tokenPair = tokenApplicationService.issueToken(member.memberUuid(),member.nickname(),member.role(),request.getDeviceId());

        TokenResponse token = new TokenResponse(tokenPair.getAccessToken());
        DataResponse<TokenResponse> response = DataResponse.ok(token);
        String refreshCookie = setRefreshTokenCookie(tokenPair.getRefreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie).body(response);
    }

    @Operation(summary = "구글 소셜 로그인 API", description = "구글 인가코드를 받아 로그인합니다.<br> 구글 인가코드를 url에서 추출할 경우 '%2F'는 '/'로 대체해야 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succeeded : Google Login"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid client input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid or missing authentication credentials"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error: An unexpected error occurred on the Authorization server")
    })
    @PostMapping("/google/social-login")
    public ResponseEntity<DataResponse<?>> googleSocialLogin(@Valid @RequestBody SocialLoginRequest request) {
        JwtUserPayload member = socialAuthApplicationService.handleSocialLogin(AuthProvider.GOOGLE, request.getCode());

        TokenPair tokenPair = tokenApplicationService.issueToken(member.memberUuid(),member.nickname(),member.role(),request.getDeviceId());

        TokenResponse token = new TokenResponse(tokenPair.getAccessToken());
        DataResponse<TokenResponse> response = DataResponse.ok(token);
        String refreshCookie = setRefreshTokenCookie(tokenPair.getRefreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie).body(response);
    }

    private String setRefreshTokenCookie(String refreshToken) {
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken).build();
        return refreshCookie.toString();
    }
}
