package kr.modusplant.domains.communication.conv.common.util.domain;

import kr.modusplant.domains.communication.conversation.domain.model.ConvCategory;

public interface ConvCategoryTestUtils {
    ConvCategory convCategory = ConvCategory.builder().order(1).category("대화 항목").build();
}
