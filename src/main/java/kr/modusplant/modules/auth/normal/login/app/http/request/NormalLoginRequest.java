package kr.modusplant.modules.auth.normal.login.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NormalLoginRequest(
        @Schema(
                description = "이메일",
                pattern = "^(?=.{2,255}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                example = "flowers32@gmail.com"
        )
        @NotBlank(message = "이메일이 비어 있습니다.")
        @Pattern(regexp = "^(?=.{2,255}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "이메일 서식이 올바르지 않습니다.")
        String email,

        @Schema(
                description = "비밀번호",
                pattern = "^(?=\\S*[a-zA-Z])(?=\\S*[!@#$%^&*+\\-=_])(?=\\S*[0-9])\\S{8,64}$",
                example = "12!excellent"
        )
        @NotBlank(message = "비밀번호가 비어 있습니다.")
        @Pattern(regexp = "^(?=\\S*[a-zA-Z])(?=\\S*[!@#$%^&*+\\-=_])(?=\\S*[0-9])\\S{8,64}$",
                message = "비밀번호는 8 ~ 64자까지 가능하며, 최소 하나 이상의 영문, 숫자, 그리고 특수문자를 포함해야 합니다(공백 제외).")
        String password) {
}
