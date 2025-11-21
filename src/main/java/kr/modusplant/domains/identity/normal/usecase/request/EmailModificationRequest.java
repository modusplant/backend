package kr.modusplant.domains.identity.normal.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static kr.modusplant.shared.constant.Regex.REGEX_EMAIL;

public record EmailModificationRequest(
        @Schema(
                description = "이메일",
                pattern = REGEX_EMAIL,
                example = "flowers32@gmail.com"
        )
        @NotBlank(message = "현재 이메일이 비어 있습니다.")
        String currentEmail,

        @Schema(
                description = "이메일",
                pattern = REGEX_EMAIL,
                example = "flowers32@gmail.com"
        )
        @NotBlank(message = "변경된 이메일이 비어 있습니다.")
        String newEmail
) {
}
