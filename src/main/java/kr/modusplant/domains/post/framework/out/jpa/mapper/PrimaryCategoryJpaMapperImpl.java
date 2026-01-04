package kr.modusplant.domains.post.framework.out.jpa.mapper;

import kr.modusplant.domains.post.domain.vo.PrimaryCategory;
import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.PrimaryCategoryJpaMapper;
import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class PrimaryCategoryJpaMapperImpl implements PrimaryCategoryJpaMapper {
    @Override
    public PrimaryCategory toPrimaryCategory(CommPrimaryCategoryEntity primaryCategoryEntity) {
        return PrimaryCategory.create(
                PrimaryCategoryId.create(primaryCategoryEntity.getId()),
                primaryCategoryEntity.getCategory(),
                primaryCategoryEntity.getOrder()
        );
    }
}
