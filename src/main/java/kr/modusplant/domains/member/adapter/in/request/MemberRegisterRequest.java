package kr.modusplant.domains.member.adapter.in.request;

import jakarta.validation.constraints.NotBlank;

import static kr.modusplant.domains.member.domain.exception.constant.MemberErrorMessage.EMPTY_MEMBER_NICKNAME;

public record MemberRegisterRequest(
        @NotBlank(message = EMPTY_MEMBER_NICKNAME)
        String nickname) {
}
