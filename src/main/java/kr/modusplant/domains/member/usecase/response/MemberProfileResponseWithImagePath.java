package kr.modusplant.domains.member.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.member.usecase.response.supers.MemberProfileResponse;

import java.util.UUID;

public record MemberProfileResponseWithImagePath(
        @Schema(description = "회원 아이디",
                type = "string",
                format = "uuid")
        UUID id,

        @Schema(description = "회원 프로필 이미지 경로")
        String imagePath,

        @Schema(description = "회원 프로필 소개",
                example = "프로필 소개글")
        String introduction,

        @Schema(description = "회원 닉네임",
                example = "ModusPlantPlayer")
        String nickname) implements MemberProfileResponse {
}
