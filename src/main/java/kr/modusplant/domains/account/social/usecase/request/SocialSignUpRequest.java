package kr.modusplant.domains.account.social.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SocialSignUpRequest(
        @Schema(description = "닉네임", pattern = "^[0-9a-zA-Z가-힣]{2,16}$", example = "여기우리함께")
        @NotBlank(message = "닉네임이 비어 있습니다.")
        @Pattern(regexp = "^[0-9a-zA-Z가-힣]{2,10}$", message = "닉네임은 2 ~ 10자까지 가능하며, 특수문자는 사용할 수 없습니다.")
        String nickname,

        @Schema(description = "프로필 소개", example = "프로필 소개")
        @Size(max = 60, message = "프로필 소개는 60자 이하여야 합니다.")
        String introduction,

        @Schema(description = "동의한 이용약관 버전", pattern = "^v\\d+.\\d+.\\d+$", example = "v1.0.12")
        @NotBlank(message = "동의한 이용약관 버전이 비어 있습니다.")
        @Pattern(regexp = "v\\d+.\\d+.\\d+$", message = "동의한 이용약관 버전의 서식이 올바르지 않습니다.")
        String agreedTermsOfUseVersion,

        @Schema(description = "동의한 개인정보처리방침 버전", pattern = "^v\\d+.\\d+.\\d+$", example = "v1.1.3")
        @NotBlank(message = "동의한 개인정보처리방침 버전이 비어 있습니다.")
        @Pattern(regexp = "v\\d+.\\d+.\\d+$", message = "동의한 개인정보처리방침 버전의 서식이 올바르지 않습니다.")
        String agreedPrivacyPolicyVersion,

        @Schema(description = "동의한 커뮤니티 운영정책 버전", pattern = "^v\\d+.\\d+.\\d+$", example = "v2.0.7")
        @NotBlank(message = "동의한 커뮤니티 운영정책 버전이 비어 있습니다.")
        @Pattern(regexp = "v\\d+.\\d+.\\d+$", message = "동의한 커뮤니티 운영정책 버전의 서식이 올바르지 않습니다.")
        String agreedCommunityPolicyVersion
) {
}
