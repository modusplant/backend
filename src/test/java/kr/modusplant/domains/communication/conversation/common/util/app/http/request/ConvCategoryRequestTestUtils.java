package kr.modusplant.domains.communication.conversation.common.util.app.http.request;

import kr.modusplant.domains.communication.conversation.app.http.request.ConvCategoryInsertRequest;
import kr.modusplant.domains.communication.conversation.common.util.domain.ConvCategoryTestUtils;

public interface ConvCategoryRequestTestUtils extends ConvCategoryTestUtils {
    ConvCategoryInsertRequest testConvCategoryInsertRequest = new ConvCategoryInsertRequest(testConvCategory.getOrder(), testConvCategory.getCategory());
}
