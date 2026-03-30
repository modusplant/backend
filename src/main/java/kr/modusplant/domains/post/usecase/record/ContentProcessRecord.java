package kr.modusplant.domains.post.usecase.record;

import com.fasterxml.jackson.databind.JsonNode;

public record ContentProcessRecord(
        JsonNode content,
        String thumbnailPath
) {
}
