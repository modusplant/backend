package kr.modusplant.domains.comment.usecase.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CommentCursorPageResponse(
        @Schema(description = "조회된 댓글 목록")
        List<CommentOfPostResponse> commentList,

        @Schema(description = "마지막 댓글 경로", example = "3.1")
        String lastPath,

        @Schema(description = "다음 댓글 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "페이지 크기", example = "20")
        int size) {
    public static CommentCursorPageResponse of(
            List<CommentOfPostResponse> commentList,
            String lastPath,
            boolean hasNext) {
        return new CommentCursorPageResponse(
                commentList,
                lastPath,
                hasNext,
                commentList.size()
        );
    }
}
