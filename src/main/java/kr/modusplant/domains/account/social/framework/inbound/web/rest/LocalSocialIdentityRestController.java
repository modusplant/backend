package kr.modusplant.domains.account.social.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.account.social.adapter.SocialIdentityTokenHelper;
import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityController;
import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityLinkController;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.record.*;
import kr.modusplant.domains.account.social.usecase.request.SocialAuthRequest;
import kr.modusplant.domains.account.social.usecase.response.SocialLoginResponse;
import kr.modusplant.infrastructure.jwt.dto.TokenPair;
import kr.modusplant.infrastructure.jwt.provider.JwtCookieProvider;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "로컬 환경 소셜 계정 API", description = "로컬환경에서의 소셜 로그인을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@Profile({"local", "dev"})
public class LocalSocialIdentityRestController {

    private final SocialIdentityController socialIdentityController;
    private final TokenService tokenService;
    private final JwtCookieProvider jwtCookieProvider;
    private final SocialIdentityTokenHelper tempTokenHelper;
    private final SocialIdentityLinkController socialIdentityLinkController;

    private long durationMs = 1800000;
    private boolean isLocal = true;

    @Operation(summary = "로컬환경 소셜 인증/로그인 API", description = "로컬환경에서만 사용하는 API입니다. <br>카카오/구글 인가코드를 받아 소셜 인증 및 로그인을 수행합니다.<br>구글 인가 코드를 URL에서 추출할 경우 '%2F'는 '/'로 대체해야 합니다.")
    @PostMapping("/local/auth/social-login/{provider}")
    public ResponseEntity<DataResponse<SocialLoginResponse>> socialLogin(
            @RequestBody @Valid SocialAuthRequest request,

            @Parameter(schema = @Schema(description = "소셜 플랫폼 제공자", example = "kakao"))
            @PathVariable
            @NotNull
            SocialProvider provider
    ) {
        SocialLoginResult socialLoginResult = socialIdentityController.handleSocialLogin(provider, request.code(), isLocal);
        String cookie;
        SocialLoginResponse loginResponse;
        switch (socialLoginResult) {
            case LoginResult login -> {
                TokenPair tokenPair = tokenService.issueToken(login.uuid(),login.nickname(), login.email(),login.role());
                cookie = jwtCookieProvider.generateRefreshTokenCookieAsString(tokenPair.refreshToken());
                loginResponse = SocialLoginResponse.login(tokenPair.accessToken());
            }
            case SocialPendingResult pending -> {
                String tempToken = tempTokenHelper.generateTempToken(pending, durationMs);
                cookie = jwtCookieProvider.generateTempTokenCookieAsString(tempToken,durationMs);
                loginResponse = switch (pending) {
                    case NeedSignupResult r -> SocialLoginResponse.needSignup(r.email(), r.nickname());
                    case NeedLinkResult r -> SocialLoginResponse.needLink(r.email(), r.nickname());
                };
            }
        }
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie).body(DataResponse.ok(loginResponse));
    }

    @Operation(summary = "로컬환경 소셜 연동 API", description = "카카오/구글 인가코드를 받아 소셜 인증 및 연동을 수행합니다")
    @PostMapping("/local/members/social/{provider}")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<DataResponse<Void>> linkSocialAccount(
            @AuthenticationPrincipal DefaultUserDetails userDetails,

            @RequestBody @Valid SocialAuthRequest request,

            @Parameter(schema = @Schema(description = "소셜 플랫폼 제공자", example = "kakao"))
            @PathVariable
            @NotNull
            SocialProvider provider
    ) {
        SocialUserInfo socialUserInfo = socialIdentityLinkController.issueSocialToken(provider,request.code(), isLocal);
        socialIdentityLinkController.linkSocialAccount(userDetails.getUuid(), provider, socialUserInfo);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "로컬환경 소셜 연동 해제 API", description = "카카오/구글 인가코드를 받아 소셜 인증 및 연동 해제를 수행합니다")
    @PostMapping("/local/members/social/{provider}/unlink")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<DataResponse<Void>> unlinkSocialAccount(
            @AuthenticationPrincipal DefaultUserDetails userDetails,

            @RequestBody @Valid SocialAuthRequest request,

            @Parameter(schema = @Schema(description = "소셜 플랫폼 제공자", example = "kakao"))
            @PathVariable
            @NotNull
            SocialProvider provider
    ) {
        SocialUserInfo socialUserInfo = socialIdentityLinkController.issueSocialToken(provider,request.code(), isLocal);
        socialIdentityLinkController.unlinkSocialAccount(userDetails.getUuid(), provider, socialUserInfo);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
