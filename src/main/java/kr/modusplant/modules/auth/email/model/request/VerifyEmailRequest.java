package kr.modusplant.modules.auth.email.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class VerifyEmailRequest {
    @Schema(description = "검증 코드", example = "123456")
    String verifyCode;
}
