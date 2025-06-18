package kr.modusplant.domains.communication.tip.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationCategory;
import kr.modusplant.global.domain.validation.ZeroBasedOrder;

public record TipCategoryInsertRequest(
        @Schema(description = "팁 항목", example = "삽목 + 포기 나누기")
        @CommunicationCategory
        String category,

        @Schema(description = "팁 항목의 렌더링 순서(0부터 시작)", example = "3")
        @ZeroBasedOrder
        Integer order) {
}