package kr.modusplant.domains.account.social.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.account.social.adapter.SocialIdentityTokenHelper;
import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityController;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.request.SocialLoginRequest;
import kr.modusplant.domains.account.social.usecase.response.*;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.provider.JwtCookieProvider;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@Tag(name = "소셜 계정 API", description = "소셜 로그인을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class SocialIdentityRestController {

    private final SocialIdentityController socialIdentityController;
    private final TokenService tokenService;
    private final JwtCookieProvider jwtCookieProvider;
    private final SocialIdentityTokenHelper tempTokenHelper;

    private long durationMs = 1800000;

    @Operation(summary = "소셜 인증/로그인 API", description = "카카오/구글 인가코드를 받아 소셜 인증 및 로그인을 수행합니다.<br>구글 인가 코드를 URL에서 추출할 경우 '%2F'는 '/'로 대체해야 합니다.")
    @PostMapping("/social-login/{provider}")
    public ResponseEntity<DataResponse<SocialLoginResponse>> socialLogin(
            @RequestBody @Valid SocialLoginRequest request,

            @Parameter(schema = @Schema(description = "소셜 플랫폼 제공자", example = "KAKAO"))
            @PathVariable
            @NotNull
            SocialProvider provider
    ) {
        String socialAccessToken = socialIdentityController.issueSocialAccessToken(provider, request.code());
        SocialLoginResult socialLoginResult = socialIdentityController.handleSocialLogin(provider, socialAccessToken);
        String cookie;
        SocialLoginResponse loginResponse;
        switch (socialLoginResult) {
            case LoginResult login -> {
                TokenPair tokenPair = tokenService.issueToken(login.uuid(),login.nickname(), login.email(),login.role());
                cookie = jwtCookieProvider.generateRefreshTokenCookieAsString(tokenPair.refreshToken());
                loginResponse = SocialLoginResponse.login(socialAccessToken);
            }
            case NeedSignupResult needSignup -> {
                String tempToken = tempTokenHelper.generateTempToken(needSignup.email(), needSignup.providerId(), needSignup.socialProvider(), socialAccessToken, durationMs);
                cookie = jwtCookieProvider.generateTempTokenCookieAsString(tempToken,durationMs);
                loginResponse = SocialLoginResponse.needSignup(needSignup.email(), needSignup.nickname());
            }
            case NeedLinkResult needLink -> {
                String tempToken = tempTokenHelper.generateTempToken(needLink.email(), needLink.providerId(), needLink.socialProvider(),socialAccessToken, durationMs);
                cookie = jwtCookieProvider.generateTempTokenCookieAsString(tempToken,durationMs);
                loginResponse = SocialLoginResponse.needLink(needLink.email(), needLink.nickname());
            }
        }
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie).body(DataResponse.ok(loginResponse));
    }

    @Operation(summary = "구글 소셜 로그인 API (테스트용)", description = "구글 인가 코드를 받아 로그인합니다.<br>구글 인가 코드를 URL에서 추출할 경우 '%2F'는 '/'로 대체해야 합니다.")
    @PostMapping("/google/social-login")
    public ResponseEntity<DataResponse<?>> googleSocialLogin(@RequestBody @Valid SocialLoginRequest request) {
        SocialAccountPayload member = socialIdentityController.handleSocialLogin(AuthProvider.GOOGLE, request.getCode());

        TokenPair tokenPair = tokenService.issueToken(member.getAccountId().getValue(), member.getNickname().getValue(), member.getEmail().getValue(), member.getRole());

        TokenResponse token = new TokenResponse(tokenPair.accessToken());
        DataResponse<TokenResponse> response = DataResponse.ok(token);
        String refreshCookie = jwtTokenProvider.generateRefreshTokenCookieAsString(tokenPair.refreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie).body(response);
    }
}
