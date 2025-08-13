package kr.modusplant.legacy.domains.communication.common.util.domain;

import kr.modusplant.legacy.domains.communication.domain.model.CommSecondaryCategory;

import java.util.UUID;

public interface CommPrimaryCategoryTestUtils {
    CommSecondaryCategory TEST_COMM_PRIMARY_CATEGORY = CommSecondaryCategory.builder()
            .category("컨텐츠 1차 항목")
            .order(1)
            .build();

    CommSecondaryCategory TEST_COMM_PRIMARY_CATEGORY_WITH_UUID = CommSecondaryCategory.builder()
            .uuid(UUID.randomUUID())
            .category(TEST_COMM_PRIMARY_CATEGORY.getCategory())
            .order(TEST_COMM_PRIMARY_CATEGORY.getOrder())
            .build();
}
