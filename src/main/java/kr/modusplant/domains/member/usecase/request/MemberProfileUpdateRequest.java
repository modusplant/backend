package kr.modusplant.domains.member.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static kr.modusplant.shared.constant.Regex.REGEX_NICKNAME;

public record MemberProfileUpdateRequest(
        @Schema(description = "갱신할 회원의 프로필 소개",
                example = "프로필 소개")
        @NotBlank(message = "회원 프로필 소개가 비어 있습니다. ")
        String intro,

        @Schema(description = "갱신할 회원의 프로필 이미지 경로",
                example = "/images/16e94f67-5abc-48d2-95a1-9cb4e78c7890.jpg")
        @NotBlank(message = "회원 프로필 이미지 경로가 비어 있습니다. ")
        String imageUrl,

        @Schema(description = "갱신할 회원의 닉네임",
                example = "NewPlayer")
        @NotBlank(message = "회원 닉네임이 비어 있습니다. ")
        @Pattern(regexp = REGEX_NICKNAME,
                message = "회원 닉네임 서식이 올바르지 않습니다. ")
        String nickname) {
}
