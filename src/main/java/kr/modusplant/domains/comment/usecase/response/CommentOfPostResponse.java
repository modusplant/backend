package kr.modusplant.domains.comment.usecase.response;

import java.time.LocalDateTime;

public record CommentOfPostResponse(
        String profileImagePath,
        String nickname,
        String path,
        String content,
        int likeCount,
        boolean isLiked,
        LocalDateTime createdAt,
        boolean isDeleted
) {
}
