package kr.modusplant.domains.post.usecase.record;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public record DraftPostReadModel(
        String ulid,
        String primaryCategory,
        String secondaryCategory,
        String title,
        JsonNode content,
        LocalDateTime updatedAt
) {
}
