package kr.modusplant.domains.search.common.util.domain.aggregate;

import kr.modusplant.domains.search.domain.aggregate.SearchPost;
import kr.modusplant.domains.search.domain.entity.SearchPostOption;
import kr.modusplant.domains.search.domain.enums.SearchPostSortCondition;
import kr.modusplant.domains.search.domain.enums.SearchPostTarget;

import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordSimilarityTestUtils.testSearchKeywordSimilarity1;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordTestUtils.testSearchKeyword;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostIdTestUtils.testSearchPostId;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostImportanceTestUtils.testSearchPostImportanceTitle;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostPublishedAtTestUtils.testSearchPostPublishedAt;

public interface SearchPostTestUtils {
    SearchPost testSearchPost = SearchPost.create(
            SearchPostOption.createRelevanceOption(testSearchPostId, testSearchPostPublishedAt,
                    testSearchPostImportanceTitle, testSearchKeywordSimilarity1),
            testSearchKeyword, SearchPostTarget.TITLE_CONTENT_COMMENT, SearchPostSortCondition.RELEVANCE);
}
