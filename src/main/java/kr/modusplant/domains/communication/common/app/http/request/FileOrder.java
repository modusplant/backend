package kr.modusplant.domains.communication.common.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.global.domain.validation.OneBasedOrder;

public record FileOrder(
        @Schema(description = "멀티파트 데이터를 구성하는 각각의 파트에 대한 파일명", example = "text_0.txt")
        String filename,

        @Schema(description = "멀티파트 데이터에서 해당 파트의 순서(1부터 시작)", example = "1")
        @OneBasedOrder
        Integer order) {
}