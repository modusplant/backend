package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;

import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.*;

public interface CommPrimaryCategoryEntityTestUtils {
    default CommPrimaryCategoryEntity createCommPrimaryCategoryEntity() {
        return CommPrimaryCategoryEntity.builder()
                .category(TEST_COMM_PRIMARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_PRIMARY_CATEGORY_ORDER)
                .build();
    }

    default CommPrimaryCategoryEntity createCommPrimaryCategoryEntityWithId() {
        return CommPrimaryCategoryEntity.builder()
                .id(TEST_COMM_PRIMARY_CATEGORY_ID)
                .category(TEST_COMM_PRIMARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_PRIMARY_CATEGORY_ORDER)
                .build();
    }
}
