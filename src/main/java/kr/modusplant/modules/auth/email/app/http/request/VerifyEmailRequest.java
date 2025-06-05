package kr.modusplant.modules.auth.email.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class VerifyEmailRequest {
    @Schema(description = "이메일 주소", example = "example@gmail.com")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "검증 코드", example = "12cA56")
    String verifyCode;
}
