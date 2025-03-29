package kr.modusplant.api.auth.email.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class EmailRequest {
    @Schema(description = "이메일 주소", example = "example@gmail.com")
    @Email(message = "Invalid email format")
    private String email;
}
