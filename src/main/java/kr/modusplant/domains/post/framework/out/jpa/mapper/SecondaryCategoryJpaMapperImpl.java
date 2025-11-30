package kr.modusplant.domains.post.framework.out.jpa.mapper;

import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.domain.vo.SecondaryCategory;
import kr.modusplant.domains.post.domain.vo.SecondaryCategoryId;
import kr.modusplant.domains.post.framework.out.jpa.mapper.supers.SecondaryCategoryJpaMapper;
import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class SecondaryCategoryJpaMapperImpl implements SecondaryCategoryJpaMapper {

    @Override
    public SecondaryCategory toSecondaryCategory(CommSecondaryCategoryEntity secondaryCategoryEntity) {
        return SecondaryCategory.create(
                SecondaryCategoryId.fromUuid(secondaryCategoryEntity.getUuid()),
                PrimaryCategoryId.fromUuid(secondaryCategoryEntity.getPrimaryCategoryEntity().getUuid()),
                secondaryCategoryEntity.getCategory(),
                secondaryCategoryEntity.getOrder()
        );
    }
}
