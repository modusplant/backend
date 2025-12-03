package kr.modusplant.domains.post.usecase.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record DraftPostResponse(
        @Schema(description = "게시글의 식별자", example = "01JXEDF9SNSMAVBY8Z3P5YXK5J")
        @JsonProperty("postId")
        String ulid,

        @Schema(description = "게시글이 속한 1차 항목", example = "팁")
        String primaryCategory,

        @Schema(description = "게시글이 속한 2차 항목", example = "물꽂이 + 잎꽂이")
        String secondaryCategory,

        @Schema(description = "게시글의 제목", example = "이거 과습인가요?")
        String title,

        @Schema(description = "게시글 컨텐츠 미리보기")
        JsonNode content,

        @Schema(description = "게시글이 업데이트된 날짜 및 시간")
        LocalDateTime updatedAt
) {
}
