package kr.modusplant.domains.communication.conv.common.util.app.http.request;

import kr.modusplant.domains.communication.conv.common.util.domain.ConvCategoryTestUtils;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvCategoryInsertRequest;

public interface ConvCategoryRequestTestUtils extends ConvCategoryTestUtils {
    ConvCategoryInsertRequest testConvCategoryInsertRequest = new ConvCategoryInsertRequest(testConvCategory.getOrder(), testConvCategory.getCategory());
}
