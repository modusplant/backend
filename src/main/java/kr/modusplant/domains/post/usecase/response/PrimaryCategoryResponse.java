package kr.modusplant.domains.post.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PrimaryCategoryResponse(
        @Schema(description = "1차 항목의 식별자", example = "1")
        Integer id,

        @Schema(description = "1차 항목의 카테고리명", example = "팁")
        String category,

        @Schema(description = "1차 항목의 순서", example = "1")
        Integer order
) {
}
