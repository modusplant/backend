package kr.modusplant.domains.account.social.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityController;
import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityLinkController;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.request.SocialAuthRequest;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "소셜 연동 API", description = "회원의 소셜 연동 및 해제를 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/members/social")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "Authorization")
public class SocialIdentityLinkRestController {

    private final SocialIdentityController socialIdentityController;
    private final SocialIdentityLinkController socialIdentityLinkController;

    @Operation(summary = "소셜 연동 API", description = "카카오/구글 인가코드를 받아 소셜 인증 및 연동을 수행합니다")
    @PostMapping("/{provider}")
    public ResponseEntity<DataResponse<Void>> linkSocialAccount(
            @AuthenticationPrincipal DefaultUserDetails userDetails,

            @RequestBody @Valid SocialAuthRequest request,

            @Parameter(schema = @Schema(description = "소셜 플랫폼 제공자", example = "kakao"))
            @PathVariable
            @NotNull
            SocialProvider provider
    ) {
        String socialAccessToken = socialIdentityController.issueSocialAccessToken(provider,request.code());
        socialIdentityLinkController.linkSocialAccount(userDetails.getUuid(), provider, socialAccessToken);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "소셜 연동 해제 API", description = "카카오/구글 인가코드를 받아 소셜 인증 및 연동 해제를 수행합니다")
    @PostMapping("/{provider}/unlink")
    public ResponseEntity<DataResponse<Void>> unlinkSocialAccount(
            @AuthenticationPrincipal DefaultUserDetails userDetails,

            @RequestBody @Valid SocialAuthRequest request,

            @Parameter(schema = @Schema(description = "소셜 플랫폼 제공자", example = "kakao"))
            @PathVariable
            @NotNull
            SocialProvider provider
    ) {
        String socialAccessToken = socialIdentityController.issueSocialAccessToken(provider,request.code());
        socialIdentityLinkController.unlinkSocialAccount(userDetails.getUuid(), provider, socialAccessToken);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
