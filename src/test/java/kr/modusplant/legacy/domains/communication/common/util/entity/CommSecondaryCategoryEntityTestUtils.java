package kr.modusplant.legacy.domains.communication.common.util.entity;

import kr.modusplant.legacy.domains.communication.common.util.domain.CommSecondaryCategoryTestUtils;
import kr.modusplant.framework.out.persistence.entity.CommSecondaryCategoryEntity;

public interface CommSecondaryCategoryEntityTestUtils extends CommSecondaryCategoryTestUtils {
    default CommSecondaryCategoryEntity createTestCommSecondaryCategoryEntity() {
        return CommSecondaryCategoryEntity.builder()
                .category(TEST_COMM_SECONDARY_CATEGORY.getCategory())
                .order(TEST_COMM_SECONDARY_CATEGORY.getOrder())
                .build();
    }

    default CommSecondaryCategoryEntity createTestCommSecondaryCategoryEntityWithUuid() {
        return CommSecondaryCategoryEntity.builder()
                .uuid(TEST_COMM_SECONDARY_CATEGORY_WITH_UUID.getUuid())
                .category(TEST_COMM_SECONDARY_CATEGORY.getCategory())
                .order(TEST_COMM_SECONDARY_CATEGORY.getOrder())
                .build();
    }
}
