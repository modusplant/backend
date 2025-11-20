package kr.modusplant.domains.comment.usecase.model;

public record CommentOfAuthorPageModel(
        String content,
        String createdAt,
        String postTitle,
        int totalCommentsOfPost
) {
}
