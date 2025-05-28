package kr.modusplant.domains.communication.tip.common.util.entity;

import kr.modusplant.domains.communication.tip.common.util.domain.TipCategoryTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;

public interface TipCategoryEntityTestUtils extends TipCategoryTestUtils {
    default TipCategoryEntity createTipCategoryEntity() {
        return TipCategoryEntity.builder()
                .order(tipCategory.getOrder())
                .category(tipCategory.getCategory())
                .build();
    }
}
