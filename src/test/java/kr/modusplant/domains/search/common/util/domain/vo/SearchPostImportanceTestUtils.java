package kr.modusplant.domains.search.common.util.domain.vo;

import kr.modusplant.domains.search.domain.vo.SearchPostImportance;

import static kr.modusplant.domains.search.common.constant.SearchIntegerConstant.*;

public interface SearchPostImportanceTestUtils {
    SearchPostImportance testSearchPostImportanceTitle = SearchPostImportance.create(TEST_SEARCH_POST_IMPORTANCE_TITLE);
    SearchPostImportance testSearchPostImportanceContent = SearchPostImportance.create(TEST_SEARCH_POST_IMPORTANCE_CONTENT);
    SearchPostImportance testSearchPostImportanceCommentContent = SearchPostImportance.create(TEST_SEARCH_POST_IMPORTANCE_COMMENT_CONTENT);
    SearchPostImportance testSearchPostImportanceOthers = SearchPostImportance.create(TEST_SEARCH_POST_IMPORTANCE_OTHERS);
}
