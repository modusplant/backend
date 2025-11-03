package kr.modusplant.domains.post.usecase.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public record PostSummaryReadModel(
        String ulid,
        String primaryCategory,
        String secondaryCategory,
        String nickname,
        String title,
        JsonNode content,
        LocalDateTime publishedAt
) {
}
