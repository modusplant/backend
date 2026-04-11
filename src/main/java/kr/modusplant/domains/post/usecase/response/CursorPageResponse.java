package kr.modusplant.domains.post.usecase.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.post.usecase.response.supers.PageResponse;

import java.util.List;

public record CursorPageResponse<T> (
        @Schema(description = "조회된 포스트")
        List<T> posts,

        @Schema(description = "다음 요청에 사용할 게시글 ID", example = "01JY3PPG5YJ41H7BPD0DSQW2RA")
        @JsonProperty("nextPostId")
        String nextUlid,

        @Schema(description = "다음 데이터 존재 여부", example = "true")
        boolean  hasNext,

        @Schema(description = "페이지 크기", example = "10")
        int size
) implements PageResponse<T> {
    public static <T> CursorPageResponse<T> of(
            List<T> posts,
            String nextUlid,
            boolean hasNext
    ) {
        return new CursorPageResponse<>(
                posts,
                nextUlid,
                hasNext,
                posts.size()
        );
    }
}
