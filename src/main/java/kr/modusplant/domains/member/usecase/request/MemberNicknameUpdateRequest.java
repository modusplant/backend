package kr.modusplant.domains.member.usecase.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MemberNicknameUpdateRequest(
        @NotNull(message = "회원 아이디가 비어 있습니다. ")
        UUID id,

        @NotNull(message = "회원 상태가 비어 있습니다. ")
        Boolean isActive,

        @NotBlank(message = "회원 닉네임이 비어 있습니다. ")
        String nickname) {
}
