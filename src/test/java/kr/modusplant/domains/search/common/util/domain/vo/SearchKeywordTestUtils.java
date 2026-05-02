package kr.modusplant.domains.search.common.util.domain.vo;

import kr.modusplant.domains.search.domain.vo.SearchKeyword;

import static kr.modusplant.domains.search.common.constant.SearchStringConstant.TEST_SEARCH_KEYWORD;

public interface SearchKeywordTestUtils {
    SearchKeyword testSearchKeyword = SearchKeyword.create(TEST_SEARCH_KEYWORD);
}
