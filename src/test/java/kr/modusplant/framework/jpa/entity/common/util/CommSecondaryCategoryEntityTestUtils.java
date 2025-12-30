package kr.modusplant.framework.jpa.entity.common.util;

import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;

import static kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity.CommSecondaryCategoryEntityBuilder;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.*;

public interface CommSecondaryCategoryEntityTestUtils extends CommPrimaryCategoryEntityTestUtils {
    default CommSecondaryCategoryEntityBuilder createCommSecondaryCategoryEntityBuilder() {
        return CommSecondaryCategoryEntity.builder()
                .category(TEST_COMM_SECONDARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_SECONDARY_CATEGORY_ORDER);
    }

    default CommSecondaryCategoryEntityBuilder createCommSecondaryCategoryEntityBuilderWithId() {
        return CommSecondaryCategoryEntity.builder()
                .id(TEST_COMM_SECONDARY_CATEGORY_ID)
                .category(TEST_COMM_SECONDARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_SECONDARY_CATEGORY_ORDER);
    }
}
