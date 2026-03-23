package kr.modusplant.domains.post.usecase.request;

import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;

import java.util.List;

public record PostCategoryRequest(
        Integer primaryCategoryId,
        List<Integer> secondaryCategoryIds
) {
    public PostCategoryRequest {
        if(primaryCategoryId == null && secondaryCategoryIds != null && !secondaryCategoryIds.isEmpty()) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
        }
    }

    public boolean isEmpty() {
        return primaryCategoryId == null && (secondaryCategoryIds == null || secondaryCategoryIds.isEmpty());
    }
}
