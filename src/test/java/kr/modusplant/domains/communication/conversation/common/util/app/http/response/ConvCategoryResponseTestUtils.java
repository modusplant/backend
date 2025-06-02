package kr.modusplant.domains.communication.conversation.common.util.app.http.response;

import kr.modusplant.domains.communication.conversation.app.http.response.ConvCategoryResponse;
import kr.modusplant.domains.communication.conversation.common.util.domain.ConvCategoryTestUtils;

public interface ConvCategoryResponseTestUtils extends ConvCategoryTestUtils {
    ConvCategoryResponse testConvCategoryResponse = new ConvCategoryResponse(testConvCategory.getOrder(), testConvCategory.getCategory());
}
