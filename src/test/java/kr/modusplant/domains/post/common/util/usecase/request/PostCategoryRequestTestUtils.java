package kr.modusplant.domains.post.common.util.usecase.request;

import kr.modusplant.domains.post.usecase.request.PostCategoryRequest;

import static kr.modusplant.shared.persistence.common.util.constant.PrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORIES_ID;

public interface PostCategoryRequestTestUtils {
    PostCategoryRequest testPostCategoryRequest = new PostCategoryRequest(TEST_COMM_PRIMARY_CATEGORY_ID, TEST_COMM_SECONDARY_CATEGORIES_ID);
}
