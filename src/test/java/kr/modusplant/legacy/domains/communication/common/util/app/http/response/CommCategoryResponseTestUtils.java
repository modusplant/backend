package kr.modusplant.legacy.domains.communication.common.util.app.http.response;

import kr.modusplant.legacy.domains.communication.app.http.response.CommCategoryResponse;

import static kr.modusplant.legacy.domains.communication.common.util.domain.CommPrimaryCategoryTestUtils.*;
import static kr.modusplant.legacy.domains.communication.common.util.domain.CommSecondaryCategoryTestUtils.*;

public interface CommCategoryResponseTestUtils {
    CommCategoryResponse TEST_COMM_PRIMARY_CATEGORY_RESPONSE = new CommCategoryResponse(TEST_COMM_PRIMARY_CATEGORY_UUID, TEST_COMM_PRIMARY_CATEGORY_CATEGORY, TEST_COMM_PRIMARY_CATEGORY_ORDER);
    CommCategoryResponse TEST_COMM_SECONDARY_CATEGORY_RESPONSE = new CommCategoryResponse(TEST_COMM_SECONDARY_CATEGORY_UUID, TEST_COMM_SECONDARY_CATEGORY_CATEGORY, TEST_COMM_SECONDARY_CATEGORY_ORDER);
}
