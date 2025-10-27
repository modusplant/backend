package kr.modusplant.domains.post.usecase.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.shared.validation.OneBasedOrder;

public record FileOrder(
        @Schema(
                description = "멀티파트 데이터를 구성하는 각각의 파트에 대한 파일명",
                example = "text_0.txt"
        )
        @NotBlank(message = "파일명이 비어 있습니다.")
        String filename,

        @Schema(
                description = "멀티파트 데이터에서 해당 파트의 순서(1부터 시작)",
                minimum = "1",
                maximum = "100",
                example = "1"
        )
        @OneBasedOrder
        Integer order) {
}