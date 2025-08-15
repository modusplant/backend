package kr.modusplant.domains.member.adapter.request;

import jakarta.validation.constraints.NotBlank;

import static kr.modusplant.domains.member.domain.exception.vo.MemberErrorMessage.EMPTY_PASSWORD;

public record MemberRegisterRequest(
        @NotBlank(message = EMPTY_PASSWORD)
        String nickname) {
}
