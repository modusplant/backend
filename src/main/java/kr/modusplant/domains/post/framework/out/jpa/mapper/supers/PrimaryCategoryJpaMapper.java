package kr.modusplant.domains.post.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.post.domain.vo.PrimaryCategory;
import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;


public interface PrimaryCategoryJpaMapper {
    PrimaryCategory toPrimaryCategory(CommPrimaryCategoryEntity primaryCategoryEntity);
}
