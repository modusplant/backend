package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.PrimaryCategoryEntity;

import static kr.modusplant.shared.persistence.common.util.constant.PrimaryCategoryConstant.*;

public interface PrimaryCategoryEntityTestUtils {
    default PrimaryCategoryEntity createPrimaryCategoryEntity() {
        return PrimaryCategoryEntity.builder()
                .category(TEST_COMM_PRIMARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_PRIMARY_CATEGORY_ORDER)
                .build();
    }

    default PrimaryCategoryEntity createPrimaryCategoryEntityWithId() {
        return PrimaryCategoryEntity.builder()
                .id(TEST_COMM_PRIMARY_CATEGORY_ID)
                .category(TEST_COMM_PRIMARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_PRIMARY_CATEGORY_ORDER)
                .build();
    }
}
