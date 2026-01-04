package kr.modusplant.domains.post.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SecondaryCategoryResponse(
        @Schema(description = "2차 항목의 식별자", example = "1")
        Integer id,

        @Schema(description = "2차 항목이 포함된 1차 항목의 식별자", example = "1")
        Integer primaryCategoryId,

        @Schema(description = "2차 항목의 카테고리명", example = "물꽂이 + 잎꽂이")
        String category,

        @Schema(description = "2차 항목의 순서", example = "1")
        Integer order
) {
}
