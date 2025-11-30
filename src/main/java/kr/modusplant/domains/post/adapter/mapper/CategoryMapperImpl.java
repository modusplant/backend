package kr.modusplant.domains.post.adapter.mapper;

import kr.modusplant.domains.post.domain.vo.PrimaryCategory;
import kr.modusplant.domains.post.domain.vo.SecondaryCategory;
import kr.modusplant.domains.post.usecase.port.mapper.CategoryMapper;
import kr.modusplant.domains.post.usecase.response.PrimaryCategoryResponse;
import kr.modusplant.domains.post.usecase.response.SecondaryCategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public PrimaryCategoryResponse toPrimaryCategoryResponse(PrimaryCategory primaryCategory) {
        return new PrimaryCategoryResponse(
                primaryCategory.getId().getValue(),
                primaryCategory.getCategoryName(),
                primaryCategory.getCategoryOrder()
        );
    }

    @Override
    public SecondaryCategoryResponse toSecondaryCategoryResponse(SecondaryCategory secondaryCategory) {
        return new SecondaryCategoryResponse(
                secondaryCategory.getId().getValue(),
                secondaryCategory.getPrimaryCategoryId().getValue(),
                secondaryCategory.getCategoryName(),
                secondaryCategory.getCategoryOrder()
        );
    }
}
