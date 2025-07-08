package kr.modusplant.domains.communication.common.util.entity;

import kr.modusplant.domains.communication.common.util.domain.CommPrimaryCategoryTestUtils;
import kr.modusplant.domains.communication.persistence.entity.CommPrimaryCategoryEntity;

public interface CommPrimaryCategoryEntityTestUtils extends CommPrimaryCategoryTestUtils {
    default CommPrimaryCategoryEntity createTestCommPrimaryCategoryEntity() {
        return CommPrimaryCategoryEntity.builder()
                .category(TEST_COMM_PRIMARY_CATEGORY.getCategory())
                .order(TEST_COMM_PRIMARY_CATEGORY.getOrder())
                .build();
    }

    default CommPrimaryCategoryEntity createTestCommPrimaryCategoryEntityWithUuid() {
        return CommPrimaryCategoryEntity.builder()
                .uuid(TEST_COMM_PRIMARY_CATEGORY_WITH_UUID.getUuid())
                .category(TEST_COMM_PRIMARY_CATEGORY.getCategory())
                .order(TEST_COMM_PRIMARY_CATEGORY.getOrder())
                .build();
    }
}
