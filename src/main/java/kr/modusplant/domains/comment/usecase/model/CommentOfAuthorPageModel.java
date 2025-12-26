package kr.modusplant.domains.comment.usecase.model;

import java.time.LocalDateTime;

public record CommentOfAuthorPageModel(
        String content,
        LocalDateTime createdAt,
        String postTitle,
        String postId,
        boolean isLiked,
        int totalCommentsOfPost
) {
}