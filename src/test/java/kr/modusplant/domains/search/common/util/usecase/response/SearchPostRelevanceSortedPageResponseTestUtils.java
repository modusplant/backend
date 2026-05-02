package kr.modusplant.domains.search.common.util.usecase.response;

import kr.modusplant.domains.search.domain.vo.SearchPostImportance;
import kr.modusplant.domains.search.usecase.response.SearchPostRelevanceSortedPageResponse;
import kr.modusplant.domains.search.usecase.response.SearchPostResponse;

import java.util.List;

import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.TEST_SEARCH_KEYWORD_SIMILARITY_1;
import static kr.modusplant.domains.search.common.util.usecase.response.SearchPostResponseTestUtils.testSearchPostResponse;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_PUBLISHED_AT;

public interface SearchPostRelevanceSortedPageResponseTestUtils {
    SearchPostRelevanceSortedPageResponse<SearchPostResponse> testSearchPostLatestSortedPageResponse =
            SearchPostRelevanceSortedPageResponse.of(
                    List.of(testSearchPostResponse), TEST_POST_ULID, TEST_COMM_POST_PUBLISHED_AT,
                    null, null, true);

    SearchPostRelevanceSortedPageResponse<SearchPostResponse> testSearchPostRelevanceSortedPageResponse =
            SearchPostRelevanceSortedPageResponse.of(
                    List.of(testSearchPostResponse), TEST_POST_ULID, TEST_COMM_POST_PUBLISHED_AT,
                    SearchPostImportance.title().getValueIfNotEmpty(), TEST_SEARCH_KEYWORD_SIMILARITY_1, true);
}
