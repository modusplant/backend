package kr.modusplant.domains.comment.usecase.response;

public record CommentOfAuthorResponse(
        String content,
        String createdAt,
        String postTitle,
        int totalCommentsOfPost
) {
}
