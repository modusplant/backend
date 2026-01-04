package kr.modusplant.domains.post.usecase.request;

import java.util.List;

public record PostCategoryRequest(
        Integer primaryCategoryId,
        List<Integer> secondaryCategoryIds
) {
}
