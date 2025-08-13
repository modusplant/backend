package kr.modusplant.legacy.domains.communication.common.util.app.http.request;

import kr.modusplant.legacy.domains.communication.app.http.request.CommCategoryInsertRequest;
import kr.modusplant.legacy.domains.communication.common.util.domain.CommPrimaryCategoryTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.domain.CommSecondaryCategoryTestUtils;

public interface CommCategoryRequestTestUtils extends CommPrimaryCategoryTestUtils, CommSecondaryCategoryTestUtils {
    CommCategoryInsertRequest TEST_COMM_PRIMARY_CATEGORY_INSERT_REQUEST = new CommCategoryInsertRequest(TEST_COMM_PRIMARY_CATEGORY.getCategory(), TEST_COMM_PRIMARY_CATEGORY.getOrder());
    CommCategoryInsertRequest TEST_COMM_SECONDARY_CATEGORY_INSERT_REQUEST = new CommCategoryInsertRequest(TEST_COMM_SECONDARY_CATEGORY.getCategory(), TEST_COMM_SECONDARY_CATEGORY.getOrder());
}
