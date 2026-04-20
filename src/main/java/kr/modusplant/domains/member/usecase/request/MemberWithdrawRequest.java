package kr.modusplant.domains.member.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.modusplant.domains.member.domain.enums.WithdrawReason;

public record MemberWithdrawRequest(
        @Schema(
                description = "인증 코드",
                example = "BPAlKjanydCLdDnYdib6MQpDRwPG7hgqgWwECDwlr_jVWR6WpNeIbGlpBKIKKiVOAAABjE6Zt5qBPKUF0hG4dQ"
        )
        String authCode,

        @Schema(
                description = "인증 제공자",
                example = "kakao"
        )
        String authProvider,

        @Schema(
                description = "탈퇴 사유",
                example = "UNCOMFORTABLE_TO_USE"
        )
        @NotNull(message = "탈퇴 사유가 비어 있습니다. ")
        WithdrawReason reason,

        @Schema(
                description = "의견",
                example = "더 좋은 프로젝트를 위해서는, "
        )
        @Size(max = 600, message = "의견이 너무 깁니다. ")
        String opinion) {
}
