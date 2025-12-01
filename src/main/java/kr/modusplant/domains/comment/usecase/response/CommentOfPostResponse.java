package kr.modusplant.domains.comment.usecase.response;

public record CommentOfPostResponse(
        String nickname,
        String path,
        String content,
        int likeCount,
        String createdAt,
        boolean isDeleted
) {
}
