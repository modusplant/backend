package kr.modusplant.domains.communication.app.http.response;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommPostResponse(
        @Schema(description = "게시글의 식별자", example = "01JXEDF9SNSMAVBY8Z3P5YXK5J")
        String ulid,

        @Schema(description = "게시글이 속한 1차 항목", example = "팁")
        String primaryCategory,

        @Schema(description = "게시글이 포함된 1차 항목의 식별자", example = "2d9f462d-b50f-4394-928e-5c864f60b09a")
        UUID primaryCategoryUuid,

        @Schema(description = "게시글이 속한 1차 항목의 렌더링 순서(0부터 시작)", example = "2")
        Integer primaryCategoryOrder,

        @Schema(description = "게시글이 속한 2차 항목", example = "물꽂이 + 잎꽂이")
        String secondaryCategory,

        @Schema(description = "게시글이 포함된 2차 항목의 식별자", example = "12d0d32d-f907-4605-a9d0-00b5669ea18a")
        UUID secondaryCategoryUuid,

        @Schema(description = "게시글이 속한 2차 항목의 렌더링 순서(0부터 시작)", example = "3")
        Integer secondaryCategoryOrder,

        @Schema(description = "게시글을 작성한 회원의 식별자", example = "4918b2c8-1890-4e27-9703-3795e0a9d6d9")
        UUID memberUuid,

        @Schema(description = "게시글을 작성한 회원의 닉네임", example = "제트드랍")
        String nickname,

        @Schema(description = "게시글의 조회 수", example = "231")
        Integer likeCount,

        @Schema(description = "게시글의 좋아요 수", example = "13")
        Long viewCount,

        @Schema(description = "게시글의 제목", example = "이거 과습인가요?")
        String title,

        @Schema(description = "게시글 컨텐츠")
        JsonNode content,

        @Schema(description = "게시글이 생성된 날짜 및 시간")
        LocalDateTime createdAt,

        @Schema(description = "게시글이 마지막으로 수정된 날짜 및 시간")
        LocalDateTime updatedAt) {
}