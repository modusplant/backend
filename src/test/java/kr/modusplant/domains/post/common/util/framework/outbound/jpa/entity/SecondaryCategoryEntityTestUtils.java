package kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity;

import kr.modusplant.domains.post.framework.outbound.jpa.entity.SecondaryCategoryEntity;

import static kr.modusplant.domains.post.common.constant.SecondaryCategoryConstant.*;
import static kr.modusplant.domains.post.framework.outbound.jpa.entity.SecondaryCategoryEntity.SecondaryCategoryEntityBuilder;

public interface SecondaryCategoryEntityTestUtils extends PrimaryCategoryEntityTestUtils {
    default SecondaryCategoryEntityBuilder createSecondaryCategoryEntityBuilder() {
        return SecondaryCategoryEntity.builder()
                .category(TEST_COMM_SECONDARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_SECONDARY_CATEGORY_ORDER);
    }

    default SecondaryCategoryEntityBuilder createSecondaryCategoryEntityBuilderWithId() {
        return SecondaryCategoryEntity.builder()
                .id(TEST_COMM_SECONDARY_CATEGORY_ID_1)
                .category(TEST_COMM_SECONDARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_SECONDARY_CATEGORY_ORDER);
    }
}
