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
import kr.modusplant.domains.account.social.usecase.request.SocialAuthRequest;
import kr.modusplant.domains.account.social.usecase.request.SocialSignUpRequest;
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

import static kr.modusplant.infrastructure.jwt.constant.CookieName.TEMP_TOKEN_COOKIE_NAME;


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
            @RequestBody @Valid SocialAuthRequest request,

            @Parameter(schema = @Schema(description = "소셜 플랫폼 제공자", example = "kakao"))
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
                loginResponse = SocialLoginResponse.login(tokenPair.accessToken());
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

    @Operation(summary = "소셜 회원가입 API", description = "소셜 인증/로그인 API에서 NEED_SIGNUP을 응답받은 경우 사용합니다.")
    @PostMapping("/social-signup")
    public ResponseEntity<DataResponse<SocialLoginResponse>> socialSignUp(
            @CookieValue(TEMP_TOKEN_COOKIE_NAME)
            String tempToken,

            @RequestBody @Valid
            SocialSignUpRequest socialSignUpRequest
    ) {
        LoginResult loginResult = socialIdentityController.createNewMember(socialSignUpRequest,tempTokenHelper.getTempTokenInfoFromClaims(tempToken));
        TokenPair tokenPair = tokenService.issueToken(loginResult.uuid(),loginResult.nickname(), loginResult.email(), loginResult.role());
        String refreshCookie = jwtCookieProvider.generateRefreshTokenCookieAsString(tokenPair.refreshToken());
        String expiredTempCookie = jwtCookieProvider.deleteTempTokenCookieAsString();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie, expiredTempCookie).body(DataResponse.ok(SocialLoginResponse.login(tokenPair.accessToken())));
    }

    @Operation(summary = "소셜 로그인 연동 API", description = "소셜 인증/로그인 API에서 NEED_LINK을 응답받은 경우 사용합니다.")
    @PostMapping("/social-link")
    public ResponseEntity<DataResponse<SocialLoginResponse>> socialLink(
            @CookieValue(TEMP_TOKEN_COOKIE_NAME)
            String tempToken
    ) {
        LoginResult loginResult = socialIdentityController.linkBasicSocialMember(tempTokenHelper.getTempTokenInfoFromClaims(tempToken));
        TokenPair tokenPair = tokenService.issueToken(loginResult.uuid(),loginResult.nickname(), loginResult.email(), loginResult.role());
        String refreshCookie = jwtCookieProvider.generateRefreshTokenCookieAsString(tokenPair.refreshToken());
        String expiredTempCookie = jwtCookieProvider.deleteTempTokenCookieAsString();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie, expiredTempCookie).body(DataResponse.ok(SocialLoginResponse.login(tokenPair.accessToken())));
    }

    @Operation(summary = "소셜 플랫폼 연결 해제 API", description = "소셜 로그인 과정이 중단되거나 연동 미선택 시 사용합니다.")
    @DeleteMapping("/social-connect")
    public ResponseEntity<DataResponse<Void>> unlinkSocialAccount(
            @CookieValue(TEMP_TOKEN_COOKIE_NAME)
            String tempToken
    ) {
        socialIdentityController.unlinkSocialAccount(tempTokenHelper.getSocialProviderFromClaims(tempToken),tempTokenHelper.getSocialAccessTokenFromClaims(tempToken));
        String expiredTempCookie = jwtCookieProvider.deleteTempTokenCookieAsString();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,expiredTempCookie).body(DataResponse.ok());
    }
}
