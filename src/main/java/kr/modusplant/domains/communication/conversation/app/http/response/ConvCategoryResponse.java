package kr.modusplant.domains.communication.conversation.app.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ConvCategoryResponse(
        @Schema(description = "항목의 식별자", example = "6c97704b-01f3-4dc1-b25b-f8be28542dde")
        UUID uuid,

        @Schema(description = "문자열 항목 값", example = "베고니아")
        String category,

        @Schema(description = "항목의 렌더링 순서(0부터 시작)", example = "2")
        Integer order) {
}