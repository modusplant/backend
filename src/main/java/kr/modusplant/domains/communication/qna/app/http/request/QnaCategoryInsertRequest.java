package kr.modusplant.domains.communication.qna.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record QnaCategoryInsertRequest(
        @Schema(description = "Q&A 항목", example = "물주기 + 흙")
        String category,

        @Schema(description = "Q&A 항목의 렌더링 순서(0부터 시작)", example = "0")
        Integer order) {
}