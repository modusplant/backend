package kr.modusplant.domains.post.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.post.domain.vo.SecondaryCategory;
import kr.modusplant.domains.post.framework.out.jpa.entity.SecondaryCategoryEntity;

public interface SecondaryCategoryJpaMapper {
    SecondaryCategory toSecondaryCategory(SecondaryCategoryEntity secondaryCategoryEntity);
}
