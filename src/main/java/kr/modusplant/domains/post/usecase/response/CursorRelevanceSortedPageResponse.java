package kr.modusplant.domains.post.usecase.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.modusplant.domains.post.usecase.response.supers.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

public record CursorRelevanceSortedPageResponse<T> (
        @Schema(description = "조회된 포스트")
        List<T> posts,

        @Schema(description = "다음 요청에 사용할 게시글 ID", example = "01JY3PPG5YJ41H7BPD0DSQW2RA")
        @JsonProperty("nextPostId")
        String nextUlid,

        @Schema(description = "다음 요청에 사용할 게시글 중요도", example = "1")
        @JsonProperty("nextPostImportance")
        Integer nextImportance,

        @Schema(description = "다음 요청에 사용할 게시글 정확도", example = "0.143253469630148")
        @JsonProperty("nextPostSimilarity")
        Double nextPostSimilarity,

        @Schema(description = "다음 요청에 사용할 게시글 발행 시점")
        @JsonProperty("nextPostPublishedAt")
        LocalDateTime nextPostPublishedAt,

        @Schema(description = "다음 데이터 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "페이지 크기", example = "10")
        int size
) implements PageResponse<T> {
    public static <T> CursorRelevanceSortedPageResponse<T> of(
            List<T> posts,
            String nextUlid,
            Integer nextImportance,
            Double nextPostSimilarity,
            LocalDateTime nextPostPublishedAt,
            boolean hasNext
    ) {
        return new CursorRelevanceSortedPageResponse<>(
                posts,
                nextUlid,
                nextImportance,
                nextPostSimilarity,
                nextPostPublishedAt,
                hasNext,
                posts.size()
        );
    }
}
