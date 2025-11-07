package kr.modusplant.framework.out.jpa.entity.common.util;

import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;

import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.*;

public interface CommPrimaryCategoryEntityTestUtils {
    default CommPrimaryCategoryEntity createCommPrimaryCategoryEntity() {
        return CommPrimaryCategoryEntity.builder()
                .category(TEST_COMM_PRIMARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_PRIMARY_CATEGORY_ORDER)
                .build();
    }

    default CommPrimaryCategoryEntity createCommPrimaryCategoryEntityWithUuid() {
        return CommPrimaryCategoryEntity.builder()
                .uuid(TEST_COMM_PRIMARY_CATEGORY_UUID)
                .category(TEST_COMM_PRIMARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_PRIMARY_CATEGORY_ORDER)
                .build();
    }
}
