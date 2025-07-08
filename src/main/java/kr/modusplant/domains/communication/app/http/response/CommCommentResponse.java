package kr.modusplant.domains.communication.app.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record CommCommentResponse(
        @Schema(description = "해당 댓글이 달린 게시글의 식별자", example = "01JXEDF9SNSMAVBY8Z3P5YXK5J")
        String postUlid,

        @Schema(description = "댓글의 구체화된 경로", example = "1/2/3/")
        String path,

        @Schema(description = "해당 댓글을 작성한 유저의 식별자", example = "b9a38a4e-ed81-4c06-a506-ebc7e99a4f38")
        UUID memberUuid,

        @Schema(description = "해당 댓글을 작성한 유저의 닉네임", example = "식집사님")
        String nickname,

        @Schema(description = "댓글 컨텐츠", example = "궁금한 점이 해결되었어요 감사합니다!")
        String content) {
}