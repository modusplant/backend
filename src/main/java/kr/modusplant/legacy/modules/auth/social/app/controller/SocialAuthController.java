package kr.modusplant.legacy.modules.auth.social.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.modusplant.global.app.http.response.DataResponse;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import kr.modusplant.legacy.modules.auth.social.app.dto.JwtUserPayload;
import kr.modusplant.legacy.modules.auth.social.app.http.request.SocialLoginRequest;
import kr.modusplant.legacy.modules.auth.social.app.service.SocialAuthApplicationService;
import kr.modusplant.legacy.modules.jwt.app.dto.TokenPair;
import kr.modusplant.legacy.modules.jwt.app.http.response.TokenResponse;
import kr.modusplant.legacy.modules.jwt.app.service.TokenApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static kr.modusplant.global.vo.DatabaseFieldName.REFRESH_TOKEN;

@Tag(name = "소셜 로그인 API", description = "소셜 로그인을 다루는 API입니다.")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class SocialAuthController {

    private final SocialAuthApplicationService socialAuthApplicationService;
    private final TokenApplicationService tokenApplicationService;

    @Operation(summary = "카카오 소셜 로그인 API", description = "카카오 인가 코드를 받아 로그인합니다.")
    @PostMapping("/kakao/social-login")
    public ResponseEntity<DataResponse<?>> kakaoSocialLogin(@RequestBody @Valid SocialLoginRequest request) {
        JwtUserPayload member = socialAuthApplicationService.handleSocialLogin(AuthProvider.KAKAO, request.getCode());

        TokenPair tokenPair = tokenApplicationService.issueToken(member.memberUuid(), member.nickname(), member.role());

        TokenResponse token = new TokenResponse(tokenPair.accessToken());
        DataResponse<TokenResponse> response = DataResponse.ok(token);
        String refreshCookie = setRefreshTokenCookie(tokenPair.refreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie).body(response);
    }

    @Operation(summary = "구글 소셜 로그인 API", description = "구글 인가 코드를 받아 로그인합니다.<br>구글 인가 코드를 URL에서 추출할 경우 '%2F'는 '/'로 대체해야 합니다.")
    @PostMapping("/google/social-login")
    public ResponseEntity<DataResponse<?>> googleSocialLogin(@RequestBody @Valid SocialLoginRequest request) {
        JwtUserPayload member = socialAuthApplicationService.handleSocialLogin(AuthProvider.GOOGLE, request.getCode());

        TokenPair tokenPair = tokenApplicationService.issueToken(member.memberUuid(), member.nickname(), member.role());

        TokenResponse token = new TokenResponse(tokenPair.accessToken());
        DataResponse<TokenResponse> response = DataResponse.ok(token);
        String refreshCookie = setRefreshTokenCookie(tokenPair.refreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie).body(response);
    }

    private String setRefreshTokenCookie(String refreshToken) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(false)       // TODO: HTTPS 적용 후 true로 변경
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();
        return refreshCookie.toString();
    }
}
