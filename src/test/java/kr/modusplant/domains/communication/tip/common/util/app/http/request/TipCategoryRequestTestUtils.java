package kr.modusplant.domains.communication.tip.common.util.app.http.request;

import kr.modusplant.domains.communication.tip.app.http.request.TipCategoryInsertRequest;
import kr.modusplant.domains.communication.tip.common.util.domain.TipCategoryTestUtils;

public interface TipCategoryRequestTestUtils extends TipCategoryTestUtils {
    TipCategoryInsertRequest testTipCategoryInsertRequest = new TipCategoryInsertRequest(testTipCategory.getCategory(), testTipCategory.getOrder());
}
