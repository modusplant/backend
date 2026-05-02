package kr.modusplant.domains.search.common.util.domain.entity;

import kr.modusplant.domains.search.domain.entity.SearchPostOption;

import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordSimilarityTestUtils.testSearchKeywordSimilarity1;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostIdTestUtils.testSearchPostId;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostImportanceTestUtils.testSearchPostImportanceTitle;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostPublishedAtTestUtils.testSearchPostPublishedAt;

public interface SearchPostOptionTestUtils {
    SearchPostOption testSearchPostOption = SearchPostOption.create(testSearchPostId, testSearchPostPublishedAt, testSearchPostImportanceTitle, testSearchKeywordSimilarity1);
}
