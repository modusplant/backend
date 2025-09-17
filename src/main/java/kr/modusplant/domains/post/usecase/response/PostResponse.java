package kr.modusplant.domains.post.usecase.response;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostResponse(
        @Schema(description = "게시글의 식별자", example = "01JXEDF9SNSMAVBY8Z3P5YXK5J")
        String ulid,

        @Schema(description = "게시글이 포함된 1차 항목의 식별자", example = "2d9f462d-b50f-4394-928e-5c864f60b09a")
        UUID primaryCategoryUuid,

        @Schema(description = "게시글이 포함된 2차 항목의 식별자", example = "12d0d32d-f907-4605-a9d0-00b5669ea18a")
        UUID secondaryCategoryUuid,

        @Schema(description = "게시글을 작성한 회원의 식별자", example = "4918b2c8-1890-4e27-9703-3795e0a9d6d9")
        UUID memberUuid,

        @Schema(description = "게시글의 조회 수", example = "231")
        int likeCount,

        @Schema(description = "게시글의 좋아요 수", example = "13")
        long viewCount,

        @Schema(description = "게시글의 제목", example = "이거 과습인가요?")
        String title,

        @Schema(description = "게시글 컨텐츠")
        JsonNode content,

        @Schema(description = "게시글의 게시 유무")
        Boolean isPublished
) {
}