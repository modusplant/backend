package kr.modusplant.legacy.modules.auth.normal.signup.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static kr.modusplant.shared.constant.Regex.*;

public record NormalSignUpRequest(
        @Schema(
                description = "이메일",
                pattern = REGEX_EMAIL,
                example = "flowers32@gmail.com"
        )
        @NotBlank(message = "이메일이 비어 있습니다.")
        @Pattern(regexp = REGEX_EMAIL,
                message = "이메일 서식이 올바르지 않습니다.")
        String email,

        @Schema(
                description = "비밀번호",
                pattern = REGEX_PASSWORD,
                example = "12!excellent"
        )
        @NotBlank(message = "비밀번호가 비어 있습니다.")
        @Pattern(regexp = REGEX_PASSWORD,
                message = "비밀번호는 8 ~ 64자까지 가능하며, 최소 하나 이상의 영문, 숫자, 그리고 특수문자를 포함해야 합니다(공백 제외).")
        String pw,

        @Schema(
                description = "닉네임",
                pattern = REGEX_NICKNAME,
                example = "여기우리함께"
        )
        @NotBlank(message = "닉네임이 비어 있습니다.")
        @Pattern(regexp = REGEX_NICKNAME,
                message = "닉네임은 2 ~ 16자까지 가능하며, 특수문자는 사용할 수 없습니다.")
        String nickname,

        @Schema(
                description = "동의한 이용약관 버전",
                pattern = REGEX_VERSION,
                example = "v1.0.12"
        )
        @NotBlank(message = "동의한 이용약관 버전이 비어 있습니다.")
        @Pattern(regexp = REGEX_VERSION,
                message = "동의한 이용약관 버전의 서식이 올바르지 않습니다.")
        String agreedTermsOfUseVersion,

        @Schema(
                description = "동의한 개인정보처리방침 버전",
                pattern = REGEX_VERSION,
                example = "v1.1.3"
        )
        @NotBlank(message = "동의한 개인정보처리방침 버전이 비어 있습니다.")
        @Pattern(regexp = REGEX_VERSION,
                message = "동의한 개인정보처리방침 버전의 서식이 올바르지 않습니다.")
        String agreedPrivacyPolicyVersion,

        @Schema(
                description = "동의한 광고성 정보 수신 버전",
                pattern = REGEX_VERSION,
                example = "v2.0.7"
        )
        @NotBlank(message = "동의한 광고성 정보 수신 버전이 비어 있습니다.")
        @Pattern(regexp = REGEX_VERSION,
                message = "동의한 광고성 정보 수신 버전의 서식이 올바르지 않습니다.")
        String agreedAdInfoReceivingVersion) {
}
