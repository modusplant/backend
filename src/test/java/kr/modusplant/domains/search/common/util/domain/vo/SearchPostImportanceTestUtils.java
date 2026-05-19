package kr.modusplant.domains.search.common.util.domain.vo;

import kr.modusplant.domains.search.domain.vo.SearchPostImportance;

public interface SearchPostImportanceTestUtils {
    SearchPostImportance testSearchPostImportanceTitle = SearchPostImportance.title();
    SearchPostImportance testSearchPostImportanceContent = SearchPostImportance.content();
    SearchPostImportance testSearchPostImportanceCommentContent = SearchPostImportance.commentContent();
    SearchPostImportance testSearchPostImportanceOthers = SearchPostImportance.others();
    SearchPostImportance testSearchPostImportanceEmpty = SearchPostImportance.empty();
}
