package kr.modusplant.domains.post.framework.outbound.jpa.mapper;

import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.domain.vo.SecondaryCategory;
import kr.modusplant.domains.post.domain.vo.SecondaryCategoryId;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.supers.SecondaryCategoryJpaMapper;
import org.springframework.stereotype.Component;

@Component
public class SecondaryCategoryJpaMapperImpl implements SecondaryCategoryJpaMapper {

    @Override
    public SecondaryCategory toSecondaryCategory(SecondaryCategoryEntity secondaryCategoryEntity) {
        return SecondaryCategory.create(
                SecondaryCategoryId.create(secondaryCategoryEntity.getId()),
                PrimaryCategoryId.create(secondaryCategoryEntity.getPrimaryCategory().getId()),
                secondaryCategoryEntity.getCategory(),
                secondaryCategoryEntity.getOrder()
        );
    }
}
