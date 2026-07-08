package kr.modusplant.domains.post.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostFileUploadUrlResponse(
        @Schema(description = "파일명", example = "image_0.jpg")
        String filename,

        @Schema(description = "파일 업로드 Presigned Url (10분간 유효) ", example = "https://wasabi/...")
        String uploadUrl,

        @Schema(description = "파일키 정보", example = "post/01KJZTNFYM08N0PRW9JGW6EX30/image/image_0_1.jpg")
        String fileKey
) {
}
