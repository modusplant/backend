package kr.modusplant.domains.member.adapter.request;

import jakarta.validation.constraints.NotBlank;

public record MemberRegisterRequest(
        @NotBlank(message = "회원 닉네임이 비어 있습니다. ")
        String nickname) {
}
