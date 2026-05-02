package kr.modusplant.domains.search.usecase.port.repository;

import kr.modusplant.domains.search.domain.enums.SearchPostTarget;
import kr.modusplant.domains.search.domain.vo.*;
import kr.modusplant.domains.search.usecase.model.read.SearchPostReadModel;

import java.util.List;
import java.util.UUID;

public interface SearchPostRepository {
    List<SearchPostReadModel> searchByKeywordWithLatest(
            SearchKeyword keyword, SearchPostTarget target,
            Integer primaryCategoryId, List<Integer> secondaryCategoryIds,
            SearchPostId searchPostId, SearchPostPublishedAt searchPostPublishedAt, int size, UUID memberId);

    List<SearchPostReadModel> searchByKeywordWithRelevance(
            SearchKeyword keyword, SearchPostTarget target,
            Integer primaryCategoryId, List<Integer> secondaryCategoryIds,
            SearchPostId searchPostId, SearchPostPublishedAt searchPostPublishedAt,
            SearchPostImportance searchPostImportance, SearchKeywordSimilarity searchKeywordSimilarity,
            int size, UUID memberId);
}
