package kr.modusplant.domains.search.common.util.domain.vo;

import kr.modusplant.domains.search.domain.vo.SearchResultListSize;

import static kr.modusplant.domains.search.common.constant.SearchIntegerConstant.TEST_SEARCH_POST_SIZE;

public interface SearchResultListSizeTestUtils {
    SearchResultListSize testSearchResultListSize = SearchResultListSize.create(TEST_SEARCH_POST_SIZE);
}
