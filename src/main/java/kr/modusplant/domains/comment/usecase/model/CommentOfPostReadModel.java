package kr.modusplant.domains.comment.usecase.model;

import java.time.LocalDateTime;

public record CommentOfPostReadModel(
        String profileImage,
        String nickname,
        String path,
        String content,
        int likeCount,
        boolean isLiked,
        LocalDateTime createdAt,
        boolean isDeleted
) {
}
