package kr.modusplant.domains.communication.conversation.app.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ConvCommentResponse(
        @Schema(description = "해당 댓글이 달린 게시글의 식별자", example = "01ARZ3NDEKTSV4RRFFQ69G5FAV")
        String postUlid,

        @Schema(description = "댓글의 구체화된 경로", example = "1/5/12/")
        String path,

        @Schema(description = "해당 댓글을 작성한 유저의 식별자", example = "d6b716f1-60f7-4c79-aeaf-37037101f126")
        UUID memberUuid,

        @Schema(description = "해당 댓글을 작성한 유저의 닉네임", example = "베고베로")
        String nickname,

        @Schema(description = "댓글 컨텐츠", example = "식물이 너무 예뻐요!")
        String content) {
}