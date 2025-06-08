package kr.modusplant.modules.auth.email.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class VerifyEmailRequest {
    @Schema(description = "검증 코드", example = "1a2b3c")
    String verifyCode;
}
