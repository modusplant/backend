package kr.modusplant.domains.post.usecase.record;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostDetailDataReadModel(
        String ulid,
        Integer primaryCategoryId,
        String primaryCategory,
        Integer secondaryCategoryId,
        String secondaryCategory,
        UUID authorUuid,
        String nickname,
        String title,
        JsonNode content,
        boolean isPublished,
        LocalDateTime publishedAt,
        LocalDateTime updatedAt
) {
}
