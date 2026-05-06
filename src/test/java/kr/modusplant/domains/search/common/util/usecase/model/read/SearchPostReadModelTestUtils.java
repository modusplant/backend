package kr.modusplant.domains.search.common.util.usecase.model.read;

import kr.modusplant.domains.search.domain.vo.SearchPostImportance;
import kr.modusplant.domains.search.usecase.model.read.SearchPostReadModel;

import java.util.List;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT_THUMBNAIL_KEY;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID2;
import static kr.modusplant.domains.search.common.constant.SearchBooleanConstant.TEST_SEARCH_POST_IS_BOOKMARKED;
import static kr.modusplant.domains.search.common.constant.SearchBooleanConstant.TEST_SEARCH_POST_IS_LIKED;
import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.TEST_SEARCH_KEYWORD_SIMILARITY_0_8;
import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.TEST_SEARCH_KEYWORD_SIMILARITY_1;
import static kr.modusplant.domains.search.common.constant.SearchIntegerConstant.TEST_SEARCH_POST_COMMENT_COUNT;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_CATEGORY;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_CATEGORY;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;

public interface SearchPostReadModelTestUtils {
    SearchPostReadModel testSearchPostReadModel1 = new SearchPostReadModel(
            TEST_POST_ULID,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_COMM_POST_TITLE,
            TEST_POST_CONTENT,
            TEST_POST_CONTENT_THUMBNAIL_KEY,
            TEST_COMM_POST_LIKE_COUNT,
            TEST_COMM_POST_PUBLISHED_AT,
            TEST_SEARCH_POST_COMMENT_COUNT,
            TEST_SEARCH_POST_IS_LIKED,
            TEST_SEARCH_POST_IS_BOOKMARKED,
            SearchPostImportance.title().getValueIfNotEmpty(),
            TEST_SEARCH_KEYWORD_SIMILARITY_1
    );

    SearchPostReadModel testSearchPostReadModel2 = new SearchPostReadModel(
            TEST_POST_ULID2,
            TEST_COMM_PRIMARY_CATEGORY_CATEGORY,
            TEST_COMM_SECONDARY_CATEGORY_CATEGORY,
            MEMBER_BASIC_USER_NICKNAME,
            TEST_COMM_POST_TITLE,
            TEST_POST_CONTENT,
            TEST_POST_CONTENT_THUMBNAIL_KEY,
            TEST_COMM_POST_LIKE_COUNT,
            TEST_COMM_POST_PUBLISHED_AT,
            TEST_SEARCH_POST_COMMENT_COUNT,
            TEST_SEARCH_POST_IS_LIKED,
            TEST_SEARCH_POST_IS_BOOKMARKED,
            SearchPostImportance.content().getValueIfNotEmpty(),
            TEST_SEARCH_KEYWORD_SIMILARITY_0_8
    );

    List<SearchPostReadModel> testSearchPostReadModelList = List.of(testSearchPostReadModel1, testSearchPostReadModel2);
}
