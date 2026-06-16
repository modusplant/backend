package kr.modusplant.domains.member.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberRoleResponse(
        @Schema(description = "회원 권한",
                example = "USER")
        String role) {
}
