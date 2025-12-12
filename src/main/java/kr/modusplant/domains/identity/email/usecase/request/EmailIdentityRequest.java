package kr.modusplant.domains.identity.email.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static kr.modusplant.shared.constant.Regex.REGEX_EMAIL;

public record EmailIdentityRequest(
        @Schema(
                description = "이메일",
                pattern = REGEX_EMAIL,
                example = "example@gmail.com"
        )
        @NotBlank(message = "이메일이 비어 있습니다.")
        @Pattern(regexp = REGEX_EMAIL,
                message = "이메일 서식이 올바르지 않습니다.")
        String email
) {
}
