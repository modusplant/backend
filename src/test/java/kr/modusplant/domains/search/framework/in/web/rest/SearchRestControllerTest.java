package kr.modusplant.domains.search.framework.in.web.rest;

import kr.modusplant.domains.search.adapter.controller.SearchController;
import kr.modusplant.domains.search.domain.enums.SearchPostSortCondition;
import kr.modusplant.domains.search.domain.enums.SearchPostTarget;
import kr.modusplant.domains.search.domain.vo.SearchPostImportance;
import kr.modusplant.domains.search.usecase.response.SearchPostRelevanceSortedPageResponse;
import kr.modusplant.domains.search.usecase.response.SearchPostResponse;
import kr.modusplant.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.TEST_SEARCH_KEYWORD_SIMILARITY_1;
import static kr.modusplant.domains.search.common.constant.SearchIntegerConstant.TEST_SEARCH_POST_SIZE;
import static kr.modusplant.domains.search.common.constant.SearchStringConstant.TEST_SEARCH_KEYWORD;
import static kr.modusplant.domains.search.common.util.usecase.response.SearchPostRelevanceSortedPageResponseTestUtils.testSearchPostRelevanceSortedPageResponse;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static kr.modusplant.infrastructure.security.common.util.SiteMemberUserDetailsTestUtils.testDefaultMemberUserDetailsBuilder;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_PUBLISHED_AT;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ID;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORIES_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class SearchRestControllerTest {
    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final SearchController searchController = Mockito.mock(SearchController.class);
    private final SearchRestController searchRestController = new SearchRestController(searchController);

    @Test
    @DisplayName("searchPostsByKeyword로 응답 반환")
    void testSearchPostsByKeyword_givenValidRequest_willReturnResponse() {
        // given
        given(searchController.searchByKeyword(any())).willReturn(testSearchPostRelevanceSortedPageResponse);

        // when
        ResponseEntity<DataResponse<SearchPostRelevanceSortedPageResponse<SearchPostResponse>>> responseEntity =
                searchRestController.searchPostsByKeyword(
                        TEST_SEARCH_KEYWORD,
                        SearchPostTarget.TITLE,
                        SearchPostSortCondition.RELEVANCE,
                        TEST_COMM_PRIMARY_CATEGORY_ID,
                        TEST_COMM_SECONDARY_CATEGORIES_ID,
                        TEST_POST_ULID,
                        TEST_COMM_POST_PUBLISHED_AT,
                        SearchPostImportance.title().getValueIfNotEmpty(),
                        TEST_SEARCH_KEYWORD_SIMILARITY_1,
                        TEST_SEARCH_POST_SIZE,
                        testDefaultMemberUserDetailsBuilder.build()
                );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("getSearchHistory로 응답 반환")
    void testGetSearchHistory_givenValidRequest_willReturnResponse() {
        // given
        given(searchController.getSearchHistory(any(), anyInt())).willReturn(List.of(TEST_SEARCH_KEYWORD));

        // when
        ResponseEntity<DataResponse<List<String>>> responseEntity =
                searchRestController.getSearchHistory(TEST_SEARCH_POST_SIZE, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).getData().getFirst())
                .isEqualTo(TEST_SEARCH_KEYWORD);
    }

    @Test
    @DisplayName("removeSearchKeyword로 응답 반환")
    void testRemoveSearchKeyword_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(searchController).deleteSearchKeyword(any(), any());

        // when
        ResponseEntity<DataResponse<Void>> responseEntity =
                searchRestController.removeSearchKeyword(TEST_SEARCH_KEYWORD, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("removeAllSearchHistory로 응답 반환")
    void testRemoveAllSearchHistory_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(searchController).deleteAllSearchHistory(any());

        // when
        ResponseEntity<DataResponse<Void>> responseEntity =
                searchRestController.removeAllSearchHistory(MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }
}