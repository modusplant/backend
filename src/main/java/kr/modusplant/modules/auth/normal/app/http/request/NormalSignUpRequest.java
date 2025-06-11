package kr.modusplant.modules.auth.normal.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record NormalSignUpRequest(
        @Schema(description = "이메일 주소", example = "flowers32@gmail.com")
        String email,

        @Schema(description = "비밀번호", example = "12!excellent")
        String pw,

        @Schema(description = "닉네임", example = "여기우리함께")
        String nickname,

        @Schema(description = "동의한 이용약관 버전", example = "v1.0.12")
        String agreedTermsOfUseVersion,

        @Schema(description = "동의한 개인정보처리방침 버전", example = "v1.1.3")
        String agreedPrivacyPolicyVersion,

        @Schema(description = "동의한 광고성 정보 수신 버전", example = "v2.0.7")
        String agreedAdInfoReceivingVersion) {
}
