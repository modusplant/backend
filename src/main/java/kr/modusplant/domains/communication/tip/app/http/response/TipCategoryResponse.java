package kr.modusplant.domains.communication.tip.app.http.response;

import java.util.UUID;

public record TipCategoryResponse(UUID uuid, String category, Integer order) {
}
