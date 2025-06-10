package kr.modusplant.domains.communication.conversation.app.http.response;

import java.util.UUID;

public record ConvCategoryResponse(UUID uuid, String category, Integer order) {
}
