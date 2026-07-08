package kr.modusplant.domains.post.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FileOrder(
        @Schema(
                description = "파일의 순서(text제외)",
                minimum = "1",
                maximum = "100",
                example = "1"
        )
        @NotNull(message = "순서가 비어 있습니다.")
        @Min(value = 1, message = "순서는 1부터 100 사이의 값이어야 합니다.")
        @Max(value = 100, message = "순서는 1부터 100 사이의 값이어야 합니다.")
        Integer order,

        @Schema(description = "업로드한 파일명", example = "image_0.png")
        @NotBlank(message = "파일명이 비어있습니다.")
        String filename,

        @Schema(
                description = "파일키 정보",
                example = "post/01KM6B54J6G4DSGHT22NJ8Z1WC/image/image_0_1.png"
        )
        @NotBlank(message = "파일키 정보가 비어있습니다.")
        String fileKey
) {
}