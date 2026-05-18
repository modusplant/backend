package kr.modusplant.domains.post.framework.out.jpa.entity.common.util;

import kr.modusplant.domains.post.framework.out.jpa.entity.SecondaryCategoryEntity;

import static kr.modusplant.domains.post.framework.out.jpa.entity.SecondaryCategoryEntity.SecondaryCategoryEntityBuilder;
import static kr.modusplant.shared.persistence.common.util.constant.SecondaryCategoryConstant.*;

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
