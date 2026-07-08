package kr.modusplant.domains.post.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PostFileUploadRequest(
        @Schema(
                description = "업로드할 파일명",
                example = "image_0.jpg"
        )
        @NotBlank(message = "파일명이 비어 있습니다.")
        String filename,

        @Schema(
                description = "업로드할 파일 타입",
                example = "image/jpeg"
        )
        @NotBlank(message = "파일 타입이 비어있습니다.")
        String contentType
) {
}
