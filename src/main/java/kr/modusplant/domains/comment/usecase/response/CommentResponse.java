package kr.modusplant.domains.comment.usecase.response;

public record CommentResponse(
        String postId,
        String path,
        String nickname,
        String content,
        boolean isDeleted,
        String createdAt
) {
}
