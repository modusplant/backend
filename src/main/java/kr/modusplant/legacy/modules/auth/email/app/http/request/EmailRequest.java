package kr.modusplant.legacy.modules.auth.email.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import static kr.modusplant.shared.constant.Regex.REGEX_EMAIL;

@Getter
public class EmailRequest {
    @Schema(
            description = "이메일",
            pattern = REGEX_EMAIL,
            example = "example@gmail.com"
    )
    @NotBlank(message = "이메일이 비어 있습니다.")
    @Pattern(regexp = REGEX_EMAIL,
            message = "이메일 서식이 올바르지 않습니다.")
    private String email;
}
