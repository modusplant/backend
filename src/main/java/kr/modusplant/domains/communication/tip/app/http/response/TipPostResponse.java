package kr.modusplant.domains.communication.tip.app.http.response;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record TipPostResponse(
        @Schema(description = "게시글의 식별자", example = "01JXEDEX5GJNBB9SAB7FB2ZG9W")
        String ulid,

        @Schema(description = "게시글이 속한 항목", example = "분갈이 + 가지치기")
        String category,

        @Schema(description = "게시글이 포함된 항목의 식별자", example = "ebf3304e-b1ae-4a06-9a4e-9d784362829a")
        UUID categoryUuid,

        @Schema(description = "게시글이 속한 항목의 렌더링 순서(0부터 시작)", example = "4")
        Integer categoryOrder,

        @Schema(description = "게시글을 작성한 회원의 식별자", example = "c0d0acf3-547f-4069-83d9-ba4eaf3cd10f")
        UUID memberUuid,

        @Schema(description = "게시글을 작성한 회원의 닉네임", example = "커피한사발")
        String nickname,

        @Schema(description = "게시글의 조회 수", example = "79")
        Integer likeCount,

        @Schema(description = "게시글의 좋아요 수", example = "6")
        Long viewCount,

        @Schema(description = "게시글의 제목", example = "흙이 마른 것을 쉽게 확인할 수 있는 방법")
        String title,

        @Schema(description = "게시글 컨텐츠")
        JsonNode content,

        @Schema(description = "게시글이 생성된 날짜 및 시간")
        LocalDateTime createdAt,

        @Schema(description = "게시글이 마지막으로 수정된 날짜 및 시간")
        LocalDateTime updatedAt) {
}