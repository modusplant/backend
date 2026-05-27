package kr.modusplant.domains.post.framework.outbound.jpa.mapper.supers;

import kr.modusplant.domains.post.domain.vo.PrimaryCategory;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PrimaryCategoryEntity;


public interface PrimaryCategoryJpaMapper {
    PrimaryCategory toPrimaryCategory(PrimaryCategoryEntity primaryCategoryEntity);
}
