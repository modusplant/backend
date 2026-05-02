package kr.modusplant.domains.search.common.util.domain.vo;

import kr.modusplant.domains.search.domain.vo.SearchPostImportance;

import static kr.modusplant.domains.search.common.constant.SearchIntegerConstant.TEST_SEARCH_POST_IMPORTANCE_TITLE;

public interface SearchPostImportanceTestUtils {
    SearchPostImportance testSearchPostImportanceTitle = SearchPostImportance.create(TEST_SEARCH_POST_IMPORTANCE_TITLE);
}
