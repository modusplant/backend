package kr.modusplant.domains.post.usecase.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record PrimaryCategoryResponse(
        @Schema(description = "1차 항목의 식별자", example = "2d9f462d-b50f-4394-928e-5c864f60b09a")
        @JsonProperty("id")
        UUID uuid,

        @Schema(description = "1차 항목의 카테고리명", example = "팁")
        String category,

        @Schema(description = "1차 항목의 순서", example = "1")
        Integer order
) {
}
