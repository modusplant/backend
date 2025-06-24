package kr.modusplant.domains.communication.qna.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationCategory;
import kr.modusplant.global.domain.validation.ZeroBasedOrder;

public record QnaCategoryInsertRequest(
        @Schema(
                description = "Q&A 항목",
                maxLength = 40,
                example = "물주기 + 흙"
        )
        @CommunicationCategory
        String category,

        @Schema(
                description = "Q&A 항목의 렌더링 순서(0부터 시작)",
                minimum = "0",
                maximum = "100",
                example = "0"
        )
        @ZeroBasedOrder
        Integer order) {
}