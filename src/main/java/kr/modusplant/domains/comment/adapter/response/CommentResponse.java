package kr.modusplant.domains.comment.adapter.response;

public record CommentResponse(
        String postId,
        String path,
        String nickname,
        String content,
        boolean isDeleted,
        String createdAt
) {
}
