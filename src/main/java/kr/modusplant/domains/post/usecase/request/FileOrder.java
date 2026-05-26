package kr.modusplant.domains.post.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FileOrder(
        @Schema(
                description = "멀티파트 데이터를 구성하는 각각의 파트에 대한 파일명",
                example = "text_0.txt"
        )
        @NotBlank(message = "파일명이 비어 있습니다.")
        String filename,

        @Schema(
                description = "멀티파트 데이터에서 해당 파트의 순서(text부터 시작)",
                minimum = "0",
                maximum = "100",
                example = "1"
        )
        @NotNull(message = "순서가 비어 있습니다.")
        @Min(value = 0, message = "순서는 0부터 100 사이의 값이어야 합니다.")
        @Max(value = 100, message = "순서는 0부터 100 사이의 값이어야 합니다.")
        Integer order) {
}