package kr.modusplant.domains.post.framework.outbound.jooq.record;

import com.fasterxml.jackson.databind.JsonNode;

public record PostIsPublishedAndContentRecord(String ulid, boolean isPublished, JsonNode content) {
}
