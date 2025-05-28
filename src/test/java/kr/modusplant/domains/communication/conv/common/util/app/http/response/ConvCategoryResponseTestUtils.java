package kr.modusplant.domains.communication.conv.common.util.app.http.response;

import kr.modusplant.domains.communication.conv.common.util.domain.ConvCategoryTestUtils;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCategoryResponse;

public interface ConvCategoryResponseTestUtils extends ConvCategoryTestUtils {
    ConvCategoryResponse convCategoryTestResponse = new ConvCategoryResponse(convCategory.getOrder(), convCategory.getCategory());
}
