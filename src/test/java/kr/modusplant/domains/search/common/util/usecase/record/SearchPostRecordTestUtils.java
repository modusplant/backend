package kr.modusplant.domains.search.common.util.usecase.record;

import kr.modusplant.domains.search.domain.enums.SearchPostSortCondition;
import kr.modusplant.domains.search.domain.enums.SearchPostTarget;
import kr.modusplant.domains.search.domain.vo.SearchPostImportance;
import kr.modusplant.domains.search.usecase.record.SearchPostRecord;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_PUBLISHED_AT;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static kr.modusplant.domains.post.common.constant.PrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ID;
import static kr.modusplant.domains.post.common.constant.SecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORIES_ID;
import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.TEST_SEARCH_KEYWORD_SIMILARITY_1;
import static kr.modusplant.domains.search.common.constant.SearchIntegerConstant.TEST_SEARCH_POST_SIZE;
import static kr.modusplant.domains.search.common.constant.SearchStringConstant.TEST_SEARCH_KEYWORD;

public interface SearchPostRecordTestUtils {
    SearchPostRecord testSearchPostRecordRelevance = new SearchPostRecord(
            TEST_SEARCH_KEYWORD, SearchPostTarget.TITLE_CONTENT_COMMENT, SearchPostSortCondition.RELEVANCE,
            TEST_COMM_PRIMARY_CATEGORY_ID, TEST_COMM_SECONDARY_CATEGORIES_ID, TEST_POST_ULID, TEST_POST_PUBLISHED_AT,
            SearchPostImportance.title().getValueIfNotEmpty(), TEST_SEARCH_KEYWORD_SIMILARITY_1, TEST_SEARCH_POST_SIZE, MEMBER_BASIC_USER_UUID);
}
