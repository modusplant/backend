package kr.modusplant.domains.communication.tip.common.util.domain;

import kr.modusplant.domains.communication.tip.domain.model.TipCategory;

public interface TipCategoryTestUtils {
    TipCategory tipCategory = TipCategory.builder().order(1).category("팁 항목").build();
}
