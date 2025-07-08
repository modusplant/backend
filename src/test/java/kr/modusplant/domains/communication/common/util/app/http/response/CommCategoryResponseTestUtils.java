package kr.modusplant.domains.communication.common.util.app.http.response;

import kr.modusplant.domains.communication.app.http.response.CommCategoryResponse;
import kr.modusplant.domains.communication.common.util.domain.CommPrimaryCategoryTestUtils;
import kr.modusplant.domains.communication.common.util.domain.CommSecondaryCategoryTestUtils;

public interface CommCategoryResponseTestUtils extends CommPrimaryCategoryTestUtils, CommSecondaryCategoryTestUtils {
    CommCategoryResponse TEST_COMM_PRIMARY_CATEGORY_RESPONSE = new CommCategoryResponse(TEST_COMM_PRIMARY_CATEGORY_WITH_UUID.getUuid(), TEST_COMM_PRIMARY_CATEGORY.getCategory(), TEST_COMM_PRIMARY_CATEGORY.getOrder());
    CommCategoryResponse TEST_COMM_SECONDARY_CATEGORY_RESPONSE = new CommCategoryResponse(TEST_COMM_SECOND_CATEGORY_WITH_UUID.getUuid(), TEST_COMM_SECONDARY_CATEGORY.getCategory(), TEST_COMM_SECONDARY_CATEGORY.getOrder());
}
