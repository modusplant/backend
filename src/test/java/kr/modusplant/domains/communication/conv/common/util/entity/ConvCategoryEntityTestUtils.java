package kr.modusplant.domains.communication.conv.common.util.entity;

import kr.modusplant.domains.communication.conv.common.util.domain.ConvCategoryTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;

public interface ConvCategoryEntityTestUtils extends ConvCategoryTestUtils {
    ConvCategoryEntity testConvCategoryEntity = ConvCategoryEntity.builder()
            .order(testConvCategory.getOrder())
            .category(testConvCategory.getCategory())
            .build();
}
