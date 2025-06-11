package kr.modusplant.domains.communication.tip.app.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record TipCommentInsertRequest(
        @Schema(description = "해당 댓글이 달린 게시글의 식별자", example = "01JXEDEX5GJNBB9SAB7FB2ZG9W")
        String postUlid,

        @Schema(description = "댓글의 구체화된 경로", example = "2/4/")
        String path,

        @Schema(description = "댓글 컨텐츠", example = "좋은 정보 감사합니다!")
        String content) {
}