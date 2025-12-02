package kr.modusplant.domains.post.usecase.port.mapper;

import kr.modusplant.domains.post.domain.vo.PrimaryCategory;
import kr.modusplant.domains.post.domain.vo.SecondaryCategory;
import kr.modusplant.domains.post.usecase.response.PrimaryCategoryResponse;
import kr.modusplant.domains.post.usecase.response.SecondaryCategoryResponse;

public interface CategoryMapper {
    PrimaryCategoryResponse toPrimaryCategoryResponse(PrimaryCategory primaryCategory);

    SecondaryCategoryResponse toSecondaryCategoryResponse(SecondaryCategory secondaryCategory);
}
