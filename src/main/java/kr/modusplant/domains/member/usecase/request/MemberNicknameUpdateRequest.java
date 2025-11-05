package kr.modusplant.domains.member.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static kr.modusplant.shared.constant.Regex.REGEX_NICKNAME;

public record MemberNicknameUpdateRequest(
        @Schema(description = "갱신할 회원의 닉네임",
                example = "NewPlayer")
        @NotBlank(message = "회원 닉네임이 비어 있습니다. ")
        @Pattern(regexp = REGEX_NICKNAME,
                message = "회원 닉네임 서식이 올바르지 않습니다. ")
        String nickname) {
}
