package kr.modusplant.domains.communication.qna.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

public record QnaCategoryInsertRequest(
        @Schema(description = "Q&A 항목", example = "물주기 + 흙")
        @Length(max = 40, message = "Category must be at maximum 40 strings.")
        @NotEmpty(message = "Category must not be empty.")
        String category,

        @Schema(description = "Q&A 항목의 렌더링 순서(0부터 시작)", example = "0")
        @Range(min = 0, max = 100, message = "Order must be range from 0 to 100.")
        @NotNull(message = "Order must not be null.")
        Integer order) {
}