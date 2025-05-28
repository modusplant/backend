package kr.modusplant.domains.communication.tip.common.util.entity;

import kr.modusplant.domains.communication.tip.common.util.domain.TipCategoryTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;

public interface TipCategoryEntityTestUtils extends TipCategoryTestUtils {
    TipCategoryEntity testTipCategoryEntity = TipCategoryEntity.builder()
            .order(testTipCategory.getOrder())
            .category(testTipCategory.getCategory())
            .build();
}
