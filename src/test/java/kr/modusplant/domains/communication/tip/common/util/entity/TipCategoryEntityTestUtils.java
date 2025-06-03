package kr.modusplant.domains.communication.tip.common.util.entity;

import kr.modusplant.domains.communication.tip.common.util.domain.TipCategoryTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;

public interface TipCategoryEntityTestUtils extends TipCategoryTestUtils {
    default TipCategoryEntity createTestTipCategoryEntity() {
        return TipCategoryEntity.builder()
                .category(testTipCategory.getCategory())
                .order(testTipCategory.getOrder())
                .build();
    }

    default TipCategoryEntity createTestTipCategoryEntityWithUuid() {
        return TipCategoryEntity.builder()
                .uuid(testTipCategoryWithUuid.getUuid())
                .category(testTipCategory.getCategory())
                .order(testTipCategory.getOrder())
                .build();
    }
}
