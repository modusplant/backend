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

    default CommSecondaryCategoryEntityBuilder createCommSecondaryCategoryEntityBuilderWithUuid() {
        return CommSecondaryCategoryEntity.builder()
                .uuid(TEST_COMM_SECONDARY_CATEGORY_UUID)
                .category(TEST_COMM_SECONDARY_CATEGORY_CATEGORY)
                .order(TEST_COMM_SECONDARY_CATEGORY_ORDER);
    }
}
