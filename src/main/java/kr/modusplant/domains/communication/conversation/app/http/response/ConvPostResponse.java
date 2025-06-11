package kr.modusplant.domains.communication.conversation.app.http.response;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConvPostResponse(
        @Schema(description = "게시글의 식별자", example = "01ARZ3NDEKTSV4RRFFQ69G5FAV")
        String ulid,

        @Schema(description = "게시글이 속한 항목", example = "제라늄")
        String category,

        @Schema(description = "게시글이 포함된 항목의 식별자", example = "5f989e30-b0b8-4e59-a733-f7b5c8d901f8")
        UUID categoryUuid,

        @Schema(description = "게시글이 속한 항목의 렌더링 순서(0부터 시작)", example = "1")
        Integer categoryOrder,

        @Schema(description = "게시글을 작성한 회원의 식별자", example = "0193f418-5cba-4dde-b43e-16a9a308c124")
        UUID memberUuid,

        @Schema(description = "게시글을 작성한 회원의 닉네임", example = "모란")
        String nickname,

        @Schema(description = "게시글의 조회 수", example = "142")
        Integer likeCount,

        @Schema(description = "게시글의 좋아요 수", example = "8")
        Long viewCount,

        @Schema(description = "게시글의 제목", example = "우리 집 식물 구경하세요~")
        String title,

        @Schema(description = "게시글 컨텐츠")
        JsonNode content,

        @Schema(description = "게시글이 생성된 날짜 및 시간")
        LocalDateTime createdAt,

        @Schema(description = "게시글이 마지막으로 수정된 날짜 및 시간")
        LocalDateTime updatedAt) {
}