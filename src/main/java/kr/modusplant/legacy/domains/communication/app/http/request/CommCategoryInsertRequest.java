package kr.modusplant.legacy.domains.communication.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.legacy.domains.communication.domain.validation.CommunicationCategory;
import kr.modusplant.shared.validation.ZeroBasedOrder;

public record CommCategoryInsertRequest(
        @Schema(
                description = "컨텐츠 항목",
                maxLength = 40,
                example = "물주기 + 흙"
        )
        @CommunicationCategory
        String category,

        @Schema(
                description = "컨텐츠 항목의 렌더링 순서(0부터 시작)",
                minimum = "0",
                maximum = "100",
                example = "0"
        )
        @ZeroBasedOrder
        Integer order) {
}