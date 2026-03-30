package kr.modusplant.domains.post.usecase.request;

import kr.modusplant.domains.post.usecase.enums.SearchOption;
import kr.modusplant.domains.post.usecase.enums.SearchSort;

public record PostSearchRequest(
        SearchOption option,
        String keyword,
        SearchSort sort,
        PostCategoryRequest category
) { }
