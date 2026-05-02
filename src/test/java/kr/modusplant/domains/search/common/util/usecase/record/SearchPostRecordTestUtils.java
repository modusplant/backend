package kr.modusplant.domains.search.common.util.usecase.record;

import kr.modusplant.domains.search.domain.enums.SearchPostSortCondition;
import kr.modusplant.domains.search.domain.enums.SearchPostTarget;
import kr.modusplant.domains.search.usecase.record.SearchPostRecord;

import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.TEST_SEARCH_KEYWORD_SIMILARITY_1;
import static kr.modusplant.domains.search.common.constant.SearchIntegerConstant.TEST_SEARCH_POST_IMPORTANCE_TITLE;
import static kr.modusplant.domains.search.common.constant.SearchIntegerConstant.TEST_SEARCH_POST_SIZE;
import static kr.modusplant.domains.search.common.constant.SearchStringConstant.TEST_SEARCH_KEYWORD;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_PUBLISHED_AT;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ID;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORIES_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface SearchPostRecordTestUtils {
    SearchPostRecord testSearchPostRecordRelevance = new SearchPostRecord(
            TEST_SEARCH_KEYWORD, SearchPostTarget.TITLE_CONTENT_COMMENT, SearchPostSortCondition.RELEVANCE,
            TEST_COMM_PRIMARY_CATEGORY_ID, TEST_COMM_SECONDARY_CATEGORIES_ID, TEST_POST_ULID, TEST_COMM_POST_PUBLISHED_AT,
            TEST_SEARCH_POST_IMPORTANCE_TITLE, TEST_SEARCH_KEYWORD_SIMILARITY_1, TEST_SEARCH_POST_SIZE, MEMBER_BASIC_USER_UUID);
}
