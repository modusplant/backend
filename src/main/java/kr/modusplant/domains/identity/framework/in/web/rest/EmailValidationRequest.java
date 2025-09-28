package kr.modusplant.domains.identity.framework.in.web.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static kr.modusplant.shared.constant.Regex.REGEX_EMAIL;

public record EmailValidationRequest(
        @Schema(
                description = "이메일",
                pattern = REGEX_EMAIL,
                example = "example@gmail.com"
        )
        @NotBlank(message = "이메일이 비어 있습니다.")
        @Pattern(regexp = REGEX_EMAIL,
                message = "이메일 서식이 올바르지 않습니다.")
        String email,

        @Schema(
        description = "검증 코드",
        pattern = "^(\\w){6}$",
        example = "12cA56"
        )
        @NotBlank(message = "코드가 비어 있습니다.")
        @Pattern(regexp = "^(\\w){6}$", message = "코드를 잘못 입력하였습니다.")
        String verifyCode
) {
}
