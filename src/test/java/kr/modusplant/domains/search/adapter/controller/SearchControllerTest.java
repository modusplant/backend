package kr.modusplant.domains.search.adapter.controller;

import kr.modusplant.domains.search.adapter.mapper.SearchMapperImpl;
import kr.modusplant.domains.search.adapter.translator.SearchPostTranslator;
import kr.modusplant.domains.search.domain.enums.SearchPostSortCondition;
import kr.modusplant.domains.search.domain.enums.SearchPostTarget;
import kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode;
import kr.modusplant.domains.search.domain.vo.SearchPostImportance;
import kr.modusplant.domains.search.usecase.port.mapper.SearchMapper;
import kr.modusplant.domains.search.usecase.port.repository.SearchPostConditionRepository;
import kr.modusplant.domains.search.usecase.port.repository.SearchPostHistoryRepository;
import kr.modusplant.domains.search.usecase.port.repository.SearchPostRepository;
import kr.modusplant.domains.search.usecase.record.SearchPostRecord;
import kr.modusplant.domains.search.usecase.response.SearchPostRelevanceSortedPageResponse;
import kr.modusplant.domains.search.usecase.response.SearchPostResponse;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT_THUMBNAIL_KEY;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.TEST_SEARCH_KEYWORD_SIMILARITY_1;
import static kr.modusplant.domains.search.common.constant.SearchIntegerConstant.TEST_SEARCH_POST_SIZE;
import static kr.modusplant.domains.search.common.constant.SearchStringConstant.TEST_SEARCH_KEYWORD;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordSimilarityTestUtils.testSearchKeywordSimilarity1;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordTestUtils.testSearchKeyword;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostIdTestUtils.testSearchPostId;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostImportanceTestUtils.testSearchPostImportanceTitle;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostPublishedAtTestUtils.testSearchPostPublishedAt;
import static kr.modusplant.domains.search.common.util.usecase.model.read.SearchPostReadModelTestUtils.testSearchPostReadModelList;
import static kr.modusplant.domains.search.common.util.usecase.record.SearchPostRecordTestUtils.testSearchPostRecordRelevance;
import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_CONTENT_JSON_NODE;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_PUBLISHED_AT;
import static kr.modusplant.shared.persistence.common.util.constant.PrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORIES_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

class SearchControllerTest {
    private final SearchPostTranslator searchPostTranslator = Mockito.mock(SearchPostTranslator.class);
    private final SearchPostRepository searchPostRepository = Mockito.mock(SearchPostRepository.class);
    private final SearchPostConditionRepository searchPostConditionRepository = Mockito.mock(SearchPostConditionRepository.class);
    private final SearchPostHistoryRepository searchPostHistoryRepository = Mockito.mock(SearchPostHistoryRepository.class);
    private final SearchMapper searchMapper = new SearchMapperImpl();
    private final SearchController searchController = new SearchController(searchPostTranslator, searchMapper, searchPostRepository, searchPostConditionRepository, searchPostHistoryRepository);

    @Test
    @DisplayName("키워드로 발행된 게시글을 최신순으로 조회")
    void testSearchByKeywordWithLatest_givenKeywordAndCursor_willReturnCursorPageResponse() {
        // given
        given(searchPostConditionRepository.isIdExist(any())).willReturn(true);
        given(searchPostConditionRepository.isIdsExist(any(), any())).willReturn(true);
        given(searchPostRepository.searchByKeywordWithRelevance(
                testSearchKeyword, SearchPostTarget.TITLE_CONTENT_COMMENT,
                TEST_COMM_PRIMARY_CATEGORY_ID, TEST_COMM_SECONDARY_CATEGORIES_ID,
                testSearchPostId, testSearchPostPublishedAt, testSearchPostImportanceTitle,
                testSearchKeywordSimilarity1, TEST_SEARCH_POST_SIZE, MEMBER_BASIC_USER_UUID))
                .willReturn(testSearchPostReadModelList);
        given(searchPostTranslator.getJsonNodeContentPreview(TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY)).willReturn(TEST_COMM_POST_CONTENT_JSON_NODE);
        willDoNothing().given(searchPostHistoryRepository).saveSearchKeyword(testSearchKeyword, MEMBER_BASIC_USER_UUID);

        // when
        SearchPostRelevanceSortedPageResponse<SearchPostResponse> result =
                searchController.searchByKeyword(testSearchPostRecordRelevance);

        // then
        assertThat(result).isNotNull();
        assertThat(result.posts()).hasSize(1);
        assertThat(result.posts().getFirst().ulid()).isEqualTo(TEST_POST_ULID);
        assertThat(result.nextUlid()).isEqualTo(TEST_POST_ULID);
        assertThat(result.hasNext()).isTrue();

        verify(searchPostRepository).searchByKeywordWithRelevance(
                testSearchKeyword, SearchPostTarget.TITLE_CONTENT_COMMENT,
                TEST_COMM_PRIMARY_CATEGORY_ID, TEST_COMM_SECONDARY_CATEGORIES_ID,
                testSearchPostId, testSearchPostPublishedAt, testSearchPostImportanceTitle,
                testSearchKeywordSimilarity1, TEST_SEARCH_POST_SIZE, MEMBER_BASIC_USER_UUID);
        verify(searchPostTranslator).getJsonNodeContentPreview(TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY);
    }

    @Test
    @DisplayName("1차 항목이 null이고 2차 항목이 존재하는 조건으로 searchByKeyword 호출 시 예외 발생")
    void testSearchByKeyword_withNullPrimaryAndNotEmptySecondary_willThrowException() {
        // given
        // primaryCategoryId를 null로 설정
        SearchPostRecord record = new SearchPostRecord(
                TEST_SEARCH_KEYWORD, SearchPostTarget.TITLE_CONTENT_COMMENT, SearchPostSortCondition.RELEVANCE,
                null, TEST_COMM_SECONDARY_CATEGORIES_ID,
                TEST_POST_ULID, TEST_COMM_POST_PUBLISHED_AT,
                SearchPostImportance.title().getValueIfNotEmpty(), TEST_SEARCH_KEYWORD_SIMILARITY_1,
                TEST_SEARCH_POST_SIZE, MEMBER_BASIC_USER_UUID);
        given(searchPostConditionRepository.isIdExist(any())).willReturn(true);

        // when
        InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                searchController.searchByKeyword(record));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SearchErrorCode.INCORRECT_SEARCH_POST_CATEGORY_ID);
    }

    @Test
    @DisplayName("존재하지 않는 1차 항목 ID로 searchByKeyword 호출 시 예외 발생")
    void testSearchByKeyword_withInvalidPrimaryId_willThrowException() {
        // given
        SearchPostRecord record = new SearchPostRecord(
                TEST_SEARCH_KEYWORD, SearchPostTarget.TITLE_CONTENT_COMMENT, SearchPostSortCondition.RELEVANCE,
                TEST_COMM_PRIMARY_CATEGORY_ID, TEST_COMM_SECONDARY_CATEGORIES_ID,
                TEST_POST_ULID, TEST_COMM_POST_PUBLISHED_AT,
                SearchPostImportance.title().getValueIfNotEmpty(), TEST_SEARCH_KEYWORD_SIMILARITY_1, TEST_SEARCH_POST_SIZE, MEMBER_BASIC_USER_UUID);
        given(searchPostConditionRepository.isIdExist(record.primaryCategoryId())).willReturn(false);

        // when
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () ->
                searchController.searchByKeyword(record));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_PRIMARY_CATEGORY);
    }

    @Test
    @DisplayName("1차 항목과 매칭되지 않는 2차 항목 ID들로 searchByKeyword 호출 시 예외 발생")
    void testSearchByKeyword_withInvalidSecondaryIds_willThrowException() {
        // given
        SearchPostRecord record = new SearchPostRecord(
                TEST_SEARCH_KEYWORD, SearchPostTarget.TITLE_CONTENT_COMMENT, SearchPostSortCondition.RELEVANCE,
                TEST_COMM_PRIMARY_CATEGORY_ID, TEST_COMM_SECONDARY_CATEGORIES_ID,
                TEST_POST_ULID, TEST_COMM_POST_PUBLISHED_AT,
                SearchPostImportance.title().getValueIfNotEmpty(), TEST_SEARCH_KEYWORD_SIMILARITY_1,
                TEST_SEARCH_POST_SIZE, MEMBER_BASIC_USER_UUID);
        given(searchPostConditionRepository.isIdExist(any())).willReturn(true);

        given(searchPostConditionRepository.isIdExist(record.primaryCategoryId())).willReturn(true);
        given(searchPostConditionRepository.isIdsExist(record.primaryCategoryId(), record.secondaryCategoryIds())).willReturn(false);

        // when
        InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                searchController.searchByKeyword(record));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SearchErrorCode.INCORRECT_SEARCH_POST_CATEGORY_ID);
    }
}