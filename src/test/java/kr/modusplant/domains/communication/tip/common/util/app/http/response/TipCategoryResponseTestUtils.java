package kr.modusplant.domains.communication.tip.common.util.app.http.response;

import kr.modusplant.domains.communication.tip.app.http.response.TipCategoryResponse;
import kr.modusplant.domains.communication.tip.common.util.domain.TipCategoryTestUtils;

public interface TipCategoryResponseTestUtils extends TipCategoryTestUtils {
    TipCategoryResponse testTipCategoryResponse = new TipCategoryResponse(testTipCategoryWithUuid.getUuid(), testTipCategory.getCategory(), testTipCategory.getOrder());
}
