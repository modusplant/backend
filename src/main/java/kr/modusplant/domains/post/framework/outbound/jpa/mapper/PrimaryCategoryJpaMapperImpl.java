package kr.modusplant.domains.post.framework.outbound.jpa.mapper;

import kr.modusplant.domains.post.domain.vo.PrimaryCategory;
import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.supers.PrimaryCategoryJpaMapper;
import org.springframework.stereotype.Component;

@Component
public class PrimaryCategoryJpaMapperImpl implements PrimaryCategoryJpaMapper {
    @Override
    public PrimaryCategory toPrimaryCategory(PrimaryCategoryEntity primaryCategoryEntity) {
        return PrimaryCategory.create(
                PrimaryCategoryId.create(primaryCategoryEntity.getId()),
                primaryCategoryEntity.getCategory(),
                primaryCategoryEntity.getOrder()
        );
    }
}
