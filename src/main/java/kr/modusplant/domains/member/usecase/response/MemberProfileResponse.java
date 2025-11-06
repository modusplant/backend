package kr.modusplant.domains.member.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record MemberProfileResponse(
        @Schema(description = "회원 아이디",
                type = "UUID")
        UUID id,

        @Schema(description = "회원 프로필 이미지",
                type = "byte[]")
        byte[] image,

        @Schema(description = "회원 프로필 소개",
                example = "프로필 소개글")
        String introduction,

        @Schema(description = "회원 닉네임",
                example = "ModusPlantPlayer")
        String nickname) {
}
