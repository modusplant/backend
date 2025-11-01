package kr.modusplant.domains.term.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SiteMemberTermCreateRequest(
        @Schema(description = "회원 아이디", example = "3566cbd8-069a-4c9d-980f-74a2599a4413")
        @NotNull(message = "회원 아이디가 비어 있습니다. ")
        UUID uuid,
        @NotBlank(message = "동의한 이용약관 버전이 비어 있습니다. ")
        String agreedTermsOfUseVersion,
        @NotBlank(message = "동의한 개인정보처리방침 버전이 비어 있습니다. ")
        String agreedPrivacyPolicyVersion,
        @NotBlank(message = "동의한 광고성 정보 수신 버전이 비어 있습니다. ")
        String agreedAdInfoReceivingVersion
) {
}
