package kr.modusplant.domains.member.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberProfilePrepareResponse(
        @Schema(description = "회원 프로필 이미지 파일 키",
                example = "member/2ca57394-03ba-4eb8-a63c-74ae0771cd4a/profile/image.png")
        String fileKey,

        @Schema(description = "회원 프로필 이미지를 업로드할 스토리지 URL")
        String storageUrl) {
}
