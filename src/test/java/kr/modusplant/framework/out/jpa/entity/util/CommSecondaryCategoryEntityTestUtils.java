package kr.modusplant.framework.out.jpa.entity.util;

import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;

import static kr.modusplant.framework.out.jpa.entity.constant.CommSecondaryCategoryConstant.*;

public interface CommSecondaryCategoryEntityTestUtils {
    default CommSecondaryCategoryEntity createTestCommSecondaryCategoryEntity() {
        return CommSecondaryCategoryEntity.builder()
                .category(TEST_COMM_SECONDARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_SECONDARY_CATEGORY_ORDER)
                .build();
    }

    default CommSecondaryCategoryEntity createTestCommSecondaryCategoryEntityWithUuid() {
        return CommSecondaryCategoryEntity.builder()
                .uuid(TEST_COMM_SECONDARY_CATEGORY_UUID)
                .category(TEST_COMM_SECONDARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_SECONDARY_CATEGORY_ORDER)
                .build();
    }
}
