package kr.modusplant.domains.search.common.util.usecase.response;

import kr.modusplant.domains.search.usecase.response.SearchPostResponse;

import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.search.common.constant.SearchBooleanConstant.TEST_SEARCH_POST_IS_BOOKMARKED;
import static kr.modusplant.domains.search.common.constant.SearchBooleanConstant.TEST_SEARCH_POST_IS_LIKED;
import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.TEST_SEARCH_KEYWORD_SIMILARITY_1;
import static kr.modusplant.domains.search.common.constant.SearchIntegerConstant.TEST_SEARCH_POST_COMMENT_COUNT;
import static kr.modusplant.domains.search.common.constant.SearchIntegerConstant.TEST_SEARCH_POST_IMPORTANCE_TITLE;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_CATEGORY;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_CATEGORY;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

public interface SearchPostResponseTestUtils {
    SearchPostResponse testSearchPostResponse = new SearchPostResponse(
            TEST_POST_ULID, TEST_COMM_PRIMARY_CATEGORY_CATEGORY, TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_NICKNAME, TEST_COMM_POST_TITLE, TEST_COMM_POST_CONTENT_JSON_NODE,
            TEST_COMM_POST_LIKE_COUNT, TEST_COMM_POST_PUBLISHED_AT, TEST_SEARCH_POST_COMMENT_COUNT,
            TEST_SEARCH_POST_IS_LIKED, TEST_SEARCH_POST_IS_BOOKMARKED, TEST_SEARCH_POST_IMPORTANCE_TITLE,
            TEST_SEARCH_KEYWORD_SIMILARITY_1);
}
