package kr.modusplant.domains.communication.conversation.common.util.entity;

import kr.modusplant.domains.communication.conversation.common.util.domain.ConvCategoryTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;

public interface ConvCategoryEntityTestUtils extends ConvCategoryTestUtils {
    default ConvCategoryEntity createTestConvCategoryEntity() {
        return ConvCategoryEntity.builder()
                .category(testConvCategory.getCategory())
                .order(testConvCategory.getOrder())
                .build();
    }

    default ConvCategoryEntity createTestConvCategoryEntityWithUuid() {
        return ConvCategoryEntity.builder()
                .uuid(testConvCategoryWithUuid.getUuid())
                .category(testConvCategory.getCategory())
                .order(testConvCategory.getOrder())
                .build();
    }
}
