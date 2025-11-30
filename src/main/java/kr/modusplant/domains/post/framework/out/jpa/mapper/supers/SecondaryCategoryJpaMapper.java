package kr.modusplant.domains.post.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.post.domain.vo.SecondaryCategory;
import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;

public interface SecondaryCategoryJpaMapper {
    SecondaryCategory toSecondaryCategory(CommSecondaryCategoryEntity secondaryCategoryEntity);
}
