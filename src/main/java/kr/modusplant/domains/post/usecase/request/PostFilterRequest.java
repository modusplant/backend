package kr.modusplant.domains.post.usecase.request;

import java.util.List;
import java.util.UUID;

public record PostFilterRequest(
        UUID primaryCategoryUuid,
        List<UUID> secondaryCategoryUuids,
        String keyword
) {
}
