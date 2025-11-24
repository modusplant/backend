package kr.modusplant.domains.identity.normal.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static kr.modusplant.shared.constant.Regex.REGEX_PASSWORD;

public record InputValidationRequest(
        @Schema(
                description = "비밀번호",
                pattern = REGEX_PASSWORD,
                example = "12!excellent"
        )
        @NotBlank(message = "비밀번호가 비어 있습니다.")
        @Pattern(regexp = REGEX_PASSWORD,
                message = "비밀번호는 8 ~ 64자까지 가능하며, 최소 하나 이상의 영문, 숫자, 그리고 특수문자를 포함해야 합니다(공백 제외).")
        String password
) {
}
