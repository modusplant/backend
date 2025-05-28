package kr.modusplant.domains.communication.conv.common.util.entity;

import kr.modusplant.domains.communication.conv.common.util.domain.ConvCategoryTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;

public interface ConvCategoryEntityTestUtils extends ConvCategoryTestUtils {
    ConvCategoryEntity convCategoryEntity = ConvCategoryEntity.builder()
            .order(convCategory.getOrder())
            .category(convCategory.getCategory())
            .build();
}
