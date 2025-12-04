package kr.modusplant.domains.post.usecase.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record SecondaryCategoryResponse(
        @Schema(description = "2차 항목의 식별자", example = "12d0d32d-f907-4605-a9d0-00b5669ea18a")
        @JsonProperty("id")
        UUID uuid,

        @Schema(description = "2차 항목이 포함된 1차 항목의 식별자", example = "2d9f462d-b50f-4394-928e-5c864f60b09a")
        UUID primaryCategoryId,

        @Schema(description = "2차 항목의 카테고리명", example = "물꽂이 + 잎꽂이")
        String category,

        @Schema(description = "2차 항목의 순서", example = "1")
        Integer order
) {
}
