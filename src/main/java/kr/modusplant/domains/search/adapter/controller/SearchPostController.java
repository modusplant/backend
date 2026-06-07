package kr.modusplant.domains.search.adapter.controller;

import kr.modusplant.domains.search.adapter.translator.SearchPostTranslator;
import kr.modusplant.domains.search.domain.aggregate.SearchPost;
import kr.modusplant.domains.search.domain.entity.SearchPostOption;
import kr.modusplant.domains.search.domain.enums.SearchPostSortCondition;
import kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode;
import kr.modusplant.domains.search.domain.vo.*;
import kr.modusplant.domains.search.usecase.model.read.SearchPostReadModel;
import kr.modusplant.domains.search.usecase.port.mapper.SearchMapper;
import kr.modusplant.domains.search.usecase.port.repository.SearchPostConditionRepository;
import kr.modusplant.domains.search.usecase.port.repository.SearchPostHistoryRepository;
import kr.modusplant.domains.search.usecase.port.repository.SearchPostRepository;
import kr.modusplant.domains.search.usecase.record.SearchPostRecord;
import kr.modusplant.domains.search.usecase.response.SearchPostRelevanceSortedPageResponse;
import kr.modusplant.domains.search.usecase.response.SearchPostResponse;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode.NOT_FOUND_PRIMARY_CATEGORY;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchPostController {
    private final SearchPostTranslator searchPostTranslator;
    private final SearchMapper searchMapper;
    private final SearchPostRepository searchPostRepository;
    private final SearchPostConditionRepository searchPostConditionRepository;
    private final SearchPostHistoryRepository searchPostHistoryRepository;

    @Transactional(readOnly = true)
    public SearchPostRelevanceSortedPageResponse<SearchPostResponse> searchByKeyword(
            SearchPostRecord record) {
        Integer primaryCategoryId = record.primaryCategoryId();
        List<Integer> secondaryCategoryIds = record.secondaryCategoryIds();
        validateBeforeSearchByKeyword(primaryCategoryId, secondaryCategoryIds);

        SearchPostOption searchPostOption;
        if (record.searchPostSortCondition().equals(SearchPostSortCondition.LATEST)) {
            searchPostOption = SearchPostOption.createLatestOption(
                    SearchPostId.create(record.lastPostUlid()),
                    SearchPostPublishedAt.create(record.lastPostPublishedAt()));
        } else {
            searchPostOption = SearchPostOption.createRelevanceOption(
                    SearchPostId.create(record.lastPostUlid()),
                    SearchPostPublishedAt.create(record.lastPostPublishedAt()),
                    SearchPostImportance.create(record.lastPostImportance()),
                    SearchKeywordSimilarity.create(record.lastPostSimilarity())
            );
        }

        SearchPost searchPost = SearchPost.create(
                searchPostOption,
                SearchKeyword.create(record.keyword()),
                record.searchPostTarget(),
                record.searchPostSortCondition()
        );

        Integer size = record.size();
        UUID memberId = record.memberId();
        List<SearchPostReadModel> readModels;

        // 페이지네이션된 읽기 모델 반환
        if (searchPost.getSearchPostSortCondition().equals(SearchPostSortCondition.LATEST)) {
            readModels = searchPostRepository.searchByKeywordWithLatest(
                    searchPost.getSearchKeyword(),
                    searchPost.getSearchPostTarget(),
                    primaryCategoryId,
                    secondaryCategoryIds,
                    searchPost.getSearchPostOption().getSearchPostId(),
                    searchPost.getSearchPostOption().getSearchPostPublishedAt(),
                    size,
                    memberId);
        } else {
            readModels = searchPostRepository.searchByKeywordWithRelevance(
                    searchPost.getSearchKeyword(),
                    searchPost.getSearchPostTarget(),
                    primaryCategoryId,
                    secondaryCategoryIds,
                    searchPost.getSearchPostOption().getSearchPostId(),
                    searchPost.getSearchPostOption().getSearchPostPublishedAt(),
                    searchPost.getSearchPostOption().getSearchPostImportance(),
                    searchPost.getSearchPostOption().getSearchKeywordSimilarity(),
                    size,
                    memberId);
        }
        boolean hasNext = readModels.size() > size;

        // 응답으로 매핑
        List<SearchPostResponse> responses =
                readModels.stream()
                        .limit(size)
                        .map(readModel ->
                                searchMapper.toSearchPostResponse(readModel,
                                        searchPostTranslator.getJsonNodeContentPreview(
                                                readModel.content(), readModel.thumbnailPath())))
                        .toList();
        searchPostHistoryRepository.saveSearchKeyword(searchPost.getSearchKeyword(), memberId);

        SearchPostResponse lastResponse = hasNext && !responses.isEmpty() ? responses.getLast() : null;
        if (lastResponse != null) {
            return SearchPostRelevanceSortedPageResponse.of(
                    responses,
                    lastResponse.ulid(),
                    lastResponse.publishedAt(),
                    lastResponse.importance(),
                    lastResponse.maxWordSimilarity(),
                    true);
        } else {
            return SearchPostRelevanceSortedPageResponse.of(
                    responses, null, null, null, null, hasNext);
        }
    }

    @Transactional(readOnly = true)
    public List<String> getSearchHistory(UUID memberId, int size) {
        return searchPostHistoryRepository.getSearchHistory(memberId, size);
    }

    public void deleteSearchKeyword(String keyword, UUID memberId) {
        searchPostHistoryRepository.removeSearchKeyword(SearchKeyword.create(keyword), memberId);
    }

    public void deleteAllSearchHistory(UUID currentMemberUuid) {
        searchPostHistoryRepository.removeAllSearchHistory(currentMemberUuid);
    }

    private void validateBeforeSearchByKeyword(Integer primaryCategoryId, List<Integer> secondaryCategoryIds) {
        if (primaryCategoryId != null && !searchPostConditionRepository.isIdExist(primaryCategoryId)) {
            throw new NotFoundEntityException(NOT_FOUND_PRIMARY_CATEGORY, "primaryCategoryId");
        }
        if (primaryCategoryId == null &&
                secondaryCategoryIds != null &&
                !secondaryCategoryIds.isEmpty()) {
            throw new InvalidValueException(
                    SearchErrorCode.INCORRECT_SEARCH_POST_CATEGORY_ID,
                    List.of("primaryCategoryId", "secondaryCategoryIds"));
        }
        if (primaryCategoryId != null && secondaryCategoryIds != null &&
                !searchPostConditionRepository.isIdsExist(primaryCategoryId, secondaryCategoryIds)) {
            throw new InvalidValueException(
                    SearchErrorCode.INCORRECT_SEARCH_POST_CATEGORY_ID,
                    List.of("primaryCategoryId", "secondaryCategoryIds"));
        }
    }
}