package kr.modusplant.domains.notification.usecase.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.notification.usecase.response.supers.PageResponse;

import java.util.List;

public record CursorPageResponse<T> (
        @Schema(description = "조회된 알림 목록")
        List<T> notifications,

        @Schema(description = "다음 요청에 사용할 알림 ID", example = "01JY3PPG5YJ41H7BPD0DSQW2RA")
        @JsonProperty("nextNotificationId")
        String nextUlid,

        @Schema(description = "다음 데이터 존재 여부", example = "true")
        boolean  hasNext,

        @Schema(description = "페이지 크기", example = "10")
        int size
) implements PageResponse<T> {
    public static <T> CursorPageResponse<T> of(
            List<T> notifications,
            String nextUlid,
            boolean hasNext
    ) {
        return new CursorPageResponse<>(
                notifications,
                nextUlid,
                hasNext,
                notifications.size()
        );
    }
}
