package kr.modusplant.domains.communication.tip.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

public record TipCategoryInsertRequest(
        @Schema(description = "팁 항목", example = "삽목 + 포기 나누기")
        @Length(max = 40, message = "Category must be at maximum 40 strings.")
        @NotEmpty(message = "Category must not be empty.")
        String category,

        @Schema(description = "팁 항목의 렌더링 순서(0부터 시작)", example = "3")
        @Range(min = 0, max = 100, message = "Order must be range from 0 to 100.")
        @NotNull(message = "Order must not be null.")
        Integer order) {
}