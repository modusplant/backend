package kr.modusplant.domains.communication.conversation.app.http.response;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConvPostResponse(
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