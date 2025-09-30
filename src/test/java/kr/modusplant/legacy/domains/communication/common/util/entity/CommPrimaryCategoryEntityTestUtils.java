package kr.modusplant.legacy.domains.communication.common.util.entity;

import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;

import static kr.modusplant.legacy.domains.communication.common.util.domain.CommPrimaryCategoryTestUtils.*;

public interface CommPrimaryCategoryEntityTestUtils {
    default CommPrimaryCategoryEntity createTestCommPrimaryCategoryEntity() {
        return CommPrimaryCategoryEntity.builder()
                .category(TEST_COMM_PRIMARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_PRIMARY_CATEGORY_ORDER)
                .build();
    }

    default CommPrimaryCategoryEntity createTestCommPrimaryCategoryEntityWithUuid() {
        return CommPrimaryCategoryEntity.builder()
                .uuid(TEST_COMM_PRIMARY_CATEGORY_UUID)
                .category(TEST_COMM_PRIMARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_PRIMARY_CATEGORY_ORDER)
                .build();
    }
}
