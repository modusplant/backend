package kr.modusplant.modules.auth.normal.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.modusplant.global.domain.validation.SemanticVersioning;

public record NormalSignUpRequest(
        @Schema(description = "이메일 주소", example = "flowers32@gmail.com")
        @NotBlank(message = "이메일이 비어 있습니다.")
        @Email(message = "이메일 서식이 올바르지 않습니다.")
        String email,

        @Schema(description = "비밀번호", example = "12!excellent")
        @NotBlank(message = "비밀번호가 비어 있습니다.")
        @Pattern(regexp = "^(?=\\S*[a-zA-Z])(?=\\S*[!@#$%^&*+\\-=_])(?=\\S*[0-9])\\S{8,64}$",
                message = "비밀번호는 8 ~ 64자까지 가능하며, 최소 하나 이상의 영문, 숫자, 그리고 특수문자를 포함해야 합니다(공백 제외).")
        String pw,

        @Schema(description = "닉네임", example = "여기우리함께")
        @NotBlank(message = "닉네임이 비어 있습니다.")
        @Pattern(regexp = "^[0-9a-zA-Z가-힣]{2,10}$",
                message = "닉네임은 2 ~ 10자까지 가능하며, 특수문자는 사용할 수 없습니다.")
        String nickname,

        @Schema(description = "동의한 이용약관 버전", example = "v1.0.12")
        @SemanticVersioning
        String agreedTermsOfUseVersion,

        @Schema(description = "동의한 개인정보처리방침 버전", example = "v1.1.3")
        @SemanticVersioning
        String agreedPrivacyPolicyVersion,

        @Schema(description = "동의한 광고성 정보 수신 버전", example = "v2.0.7")
        @SemanticVersioning
        String agreedAdInfoReceivingVersion) {
}
