package kr.modusplant.domains.communication.conversation.common.util.domain;

import kr.modusplant.domains.communication.conversation.domain.model.ConvCategory;

import java.util.UUID;

public interface ConvCategoryTestUtils {
    ConvCategory testConvCategory = ConvCategory.builder()
            .category("대화 항목")
            .order(1)
            .build();

    ConvCategory testConvCategoryWithUuid = ConvCategory.builder()
            .uuid(UUID.randomUUID())
            .category(testConvCategory.getCategory())
            .order(testConvCategory.getOrder())
            .build();
}
