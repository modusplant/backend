package kr.modusplant.domains.communication.tip.app.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record TipCategoryResponse(
        @Schema(description = "항목의 식별자", example = "e250e6f1-8b9a-4436-b893-387220ce8e31")
        UUID uuid,

        @Schema(description = "문자열 항목 값", example = "물꽂이 + 잎꽂이")
        String category,

        @Schema(description = "항목의 렌더링 순서(0부터 시작)", example = "2")
        Integer order) {
}