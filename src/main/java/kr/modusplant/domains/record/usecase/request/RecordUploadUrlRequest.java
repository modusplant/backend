package kr.modusplant.domains.record.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecordUploadUrlRequest(
        @Schema(
                description = "클라이언트가 업로드할 편집 완료 영상 파일명",
                example = "새잎.mp4"
        )
        @NotBlank(message = "파일명이 비어 있습니다.")
        @Size(max = 255, message = "파일명은 최대 255자까지 허용됩니다.")
        String originalFilename,

        @Schema(
                description = "영상 MIME 타입",
                example = "video/mp4",
                allowableValues = {"video/mp4", "video/quicktime"}
        )
        @NotBlank(message = "파일 타입이 비어 있습니다.")
        String contentType,

        @Schema(
                description = "영상 파일 크기 (바이트). 이진 기준 최소 1,024 (1KB) ~ 최대 5,242,880 (5MB)",
                example = "2400000"
        )
        @NotNull(message = "파일 크기가 비어 있습니다.")
        @Min(value = 1024L, message = "파일 크기는 최소 1KB 이상이어야 합니다.")
        @Max(value = 5_242_880L, message = "파일 크기는 최대 5MB 이하여야 합니다.")
        Long sizeBytes
) {
}
