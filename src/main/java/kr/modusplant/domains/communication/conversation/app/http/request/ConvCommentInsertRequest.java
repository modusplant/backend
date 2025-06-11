package kr.modusplant.domains.communication.conversation.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ConvCommentInsertRequest(
        @Schema(description = "해당 댓글이 달린 게시글의 식별자", example = "01ARZ3NDEKTSV4RRFFQ69G5FAV")
        String postUlid,

        @Schema(description = "댓글의 구체화된 경로", example = "1/5/12/")
        String path,

        @Schema(description = "댓글 컨텐츠", example = "식물이 너무 예뻐요!")
        String content) {
}