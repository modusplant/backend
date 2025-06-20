package kr.modusplant.modules.auth.email.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailRequest {
    @Schema(description = "이메일", example = "example@gmail.com")
    @NotBlank(message = "이메일이 비어 있습니다.")
    @Email(message = "이메일 서식이 올바르지 않습니다.")
    private String email;
}
