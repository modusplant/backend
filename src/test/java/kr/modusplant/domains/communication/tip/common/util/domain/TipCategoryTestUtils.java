package kr.modusplant.domains.communication.tip.common.util.domain;

import kr.modusplant.domains.communication.tip.domain.model.TipCategory;

import java.util.UUID;

public interface TipCategoryTestUtils {
    TipCategory testTipCategory = TipCategory.builder()
            .category("팁 항목")
            .order(1)
            .build();

    TipCategory testTipCategoryWithUuid = TipCategory.builder()
            .uuid(UUID.randomUUID())
            .category(testTipCategory.getCategory())
            .order(testTipCategory.getOrder())
            .build();
}
