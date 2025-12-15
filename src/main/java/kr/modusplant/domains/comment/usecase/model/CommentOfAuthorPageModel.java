package kr.modusplant.domains.comment.usecase.model;

import java.time.LocalDate;

public record CommentOfAuthorPageModel(
        String content,
        LocalDate createdAt,
        String postTitle,
        String postId,
        boolean isLiked,
        int totalCommentsOfPost
) {
}