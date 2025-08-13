package kr.modusplant.legacy.domains.communication.common.util.domain;

import kr.modusplant.legacy.domains.communication.domain.model.CommSecondaryCategory;

import java.util.UUID;

public interface CommSecondaryCategoryTestUtils {
    CommSecondaryCategory TEST_COMM_SECONDARY_CATEGORY = CommSecondaryCategory.builder()
            .category("컨텐츠 2차 항목")
            .order(2)
            .build();

    CommSecondaryCategory TEST_COMM_SECONDARY_CATEGORY_WITH_UUID = CommSecondaryCategory.builder()
            .uuid(UUID.randomUUID())
            .category(TEST_COMM_SECONDARY_CATEGORY.getCategory())
            .order(TEST_COMM_SECONDARY_CATEGORY.getOrder())
            .build();
}
