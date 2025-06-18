package kr.modusplant.domains.communication.conversation.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationCategory;
import kr.modusplant.global.domain.validation.ZeroBasedOrder;

public record ConvCategoryInsertRequest(
        @Schema(description = "대화 항목", example = "제라늄")
        @CommunicationCategory
        String category,

        @Schema(description = "대화 항목의 렌더링 순서(0부터 시작)", example = "1")
        @ZeroBasedOrder
        Integer order) {
}