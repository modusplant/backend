package kr.modusplant.domains.record.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecordCreateResponse(
        @Schema(description = "등록 완료된 기록 ULID", example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
        String recordId
) {
}
