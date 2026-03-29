package kr.modusplant.domains.post.usecase.record;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public record PostSummaryReadModel(
        String ulid,
        String primaryCategory,
        String secondaryCategory,
        String nickname,
        String title,
        JsonNode content,
        String thumbnailPath,
        Integer likeCount,
        LocalDateTime publishedAt,
        Integer commentCount,
        Boolean isLiked,
        Boolean isBookmarked
) {
}
