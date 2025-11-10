package kr.modusplant.domains.member.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

public record MemberResponse(
        @Schema(description = "회원 아이디",
                type = "string",
                format = "uuid")
        UUID id,

        @Schema(description = "회원 상태",
                example = "active")
        String status,

        @Schema(description = "회원 닉네임",
                example = "ModusPlantPlayer")
        String nickname,

        @Schema(description = "회원 생일",
                type = "string",
                format = "date")
        LocalDate birthDate) {
}
