package kr.modusplant.domains.communication.qna.app.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record QnaCategoryResponse(
        @Schema(description = "항목의 식별자", example = "12d0d32d-f907-4605-a9d0-00b5669ea18a")
        UUID uuid,

        @Schema(description = "문자열 항목 값", example = "잎상태 + 성장 + 병충해")
        String category,

        @Schema(description = "항목의 렌더링 순서(0부터 시작)", example = "1")
        Integer order) {
}