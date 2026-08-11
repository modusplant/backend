package kr.modusplant.domains.search.usecase.port.repository;

import kr.modusplant.domains.search.domain.enums.SearchPostTarget;
import kr.modusplant.domains.search.domain.vo.*;
import kr.modusplant.domains.search.usecase.model.read.SearchPostReadModel;

import java.util.List;

public interface SearchPostRepository {
    List<SearchPostReadModel> searchByKeywordWithLatest(
            SearchKeyword keyword, SearchPostTarget target,
            Integer primaryCategoryId, List<Integer> secondaryCategoryIds,
            SearchPostId searchPostId, SearchPostPublishedAt searchPostPublishedAt,
            SearchResultListSize searchResultListSize, SearcherId searcherIdVO);

    List<SearchPostReadModel> searchByKeywordWithRelevance(
            SearchKeyword keyword, SearchPostTarget target,
            Integer primaryCategoryId, List<Integer> secondaryCategoryIds,
            SearchPostId searchPostId, SearchPostPublishedAt searchPostPublishedAt,
            SearchPostImportance searchPostImportance, SearchKeywordSimilarity searchKeywordSimilarity,
            SearchResultListSize searchResultListSize, SearcherId searcherIdVO);
}
