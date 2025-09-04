package kr.modusplant.domains.comment.adapter.response;

public record CommentResponse(
        String postId,
        String path,
        String memberNickname,
        String content,
        boolean isDeleted,
        String createdAt
) {
}
