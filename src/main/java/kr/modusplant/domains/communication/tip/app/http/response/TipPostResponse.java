package kr.modusplant.domains.communication.tip.app.http.response;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.UUID;

public record TipPostResponse(
        String ulid,
        String category,
        UUID categoryUuid,
        Integer categoryOrder,
        UUID memberUuid,
        String nickname,
        Integer likeCount,
        Long viewCount,
        String title,
        JsonNode content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}