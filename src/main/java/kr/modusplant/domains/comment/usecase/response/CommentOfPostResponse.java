package kr.modusplant.domains.comment.usecase.response;

import java.time.LocalDate;

public record CommentOfPostResponse(
        String profileImage,
        String nickname,
        String path,
        String content,
        int likeCount,
        boolean isLiked,
        LocalDate createdAt,
        boolean isDeleted
) {
}
