package kr.modusplant.domains.search.common.util.domain.vo;

import kr.modusplant.domains.search.domain.vo.SearchKeywordSimilarity;

import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.*;

public interface SearchKeywordSimilarityTestUtils {
    SearchKeywordSimilarity testSearchKeywordSimilarity1 = SearchKeywordSimilarity.create(TEST_SEARCH_KEYWORD_SIMILARITY_1);
    SearchKeywordSimilarity testSearchKeywordSimilarity08 = SearchKeywordSimilarity.create(TEST_SEARCH_KEYWORD_SIMILARITY_0_8);
    SearchKeywordSimilarity testSearchKeywordSimilarity06 = SearchKeywordSimilarity.create(TEST_SEARCH_KEYWORD_SIMILARITY_0_6);
}
