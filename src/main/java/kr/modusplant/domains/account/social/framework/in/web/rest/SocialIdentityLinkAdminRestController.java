package kr.modusplant.domains.account.social.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityLinkAdminController;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "소셜 연동 API(관리자 전용)", description = "회원의 소셜 연동 및 해제를 다루는 API입니다.")
@RestController
@RequestMapping("/api/admin/v1/members/social")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "Authorization")
public class SocialIdentityLinkAdminRestController {
    private final SocialIdentityLinkAdminController socialIdentityLinkAdminController;

    @Operation(summary = "소셜 연동 데이터 제거 API", description = "회원의 소셜 연동 데이터를 제거하고 일반 계정으로 만듭니다.")
    @DeleteMapping("/social-link")
    public ResponseEntity<DataResponse<Void>> removeSocialLinkData(
            @Parameter(hidden = true)
            @NotNull(message = "회원 ID를 찾을 수 없습니다. ")
            @AuthenticationPrincipal(expression = "uuid")
            UUID memberId
    ) {
        socialIdentityLinkAdminController.removeSocialLink(memberId);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
