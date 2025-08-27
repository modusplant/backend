package kr.modusplant.domains.member.adapter.in.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import static kr.modusplant.domains.member.domain.exception.constant.MemberErrorMessage.*;

public record MemberNicknameUpdateRequest(
        @NotNull(message = EMPTY_MEMBER_ID)
        UUID id,

        @NotNull(message = EMPTY_MEMBER_STATUS)
        Boolean isActive,

        @NotBlank(message = EMPTY_MEMBER_NICKNAME)
        String nickname) {
}
