package kr.modusplant.domains.communication.tip.common.util.app.http.response;

import kr.modusplant.domains.communication.tip.app.http.response.TipCategoryResponse;
import kr.modusplant.domains.communication.tip.common.util.domain.TipCategoryTestUtils;

public interface TipCategoryResponseTestUtils extends TipCategoryTestUtils {
    TipCategoryResponse tipCategoryTestResponse = new TipCategoryResponse(tipCategory.getOrder(), tipCategory.getCategory());
}
