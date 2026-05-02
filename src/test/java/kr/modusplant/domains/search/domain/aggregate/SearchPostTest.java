package kr.modusplant.domains.search.domain.aggregate;

import kr.modusplant.domains.search.domain.entity.SearchPostOption;
import kr.modusplant.domains.search.domain.enums.SearchPostSortCondition;
import kr.modusplant.domains.search.domain.enums.SearchPostTarget;
import kr.modusplant.domains.search.domain.vo.SearchKeywordSimilarity;
import kr.modusplant.domains.search.domain.vo.SearchPostId;
import kr.modusplant.domains.search.domain.vo.SearchPostImportance;
import kr.modusplant.domains.search.domain.vo.SearchPostPublishedAt;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID2;
import static kr.modusplant.domains.search.common.util.domain.aggregate.SearchPostTestUtils.testSearchPost;
import static kr.modusplant.domains.search.common.util.domain.entity.SearchPostOptionTestUtils.testSearchPostOption;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordSimilarityTestUtils.testSearchKeywordSimilarity1;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordTestUtils.testSearchKeyword;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostIdTestUtils.testSearchPostId;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostImportanceTestUtils.testSearchPostImportanceTitle;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostPublishedAtTestUtils.testSearchPostPublishedAt;
import static kr.modusplant.domains.search.common.util.domain.vo.nullobject.EmptySearchPostIdTestUtils.testEmptySearchPostId;
import static kr.modusplant.domains.search.common.util.domain.vo.nullobject.EmptySearchPostPublishedAtTestUtils.testEmptySearchPostPublishedAt;
import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.*;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_PUBLISHED_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearchPostTest {
    private final SearchPostTarget testSearchPostTarget = SearchPostTarget.TITLE_CONTENT_COMMENT;

    @Test
    @DisplayName("null 값으로 create 호출")
    void testCreate_givenNullToOneOfFourParameters_willThrowException() {
        // SearchPostOption이 null일 때
        // given & when
        EmptyValueException optionException = assertThrows(EmptyValueException.class,
                () -> SearchPost.create(null, testSearchKeyword, testSearchPostTarget, SearchPostSortCondition.LATEST));

        // then
        assertThat(optionException.getErrorCode()).isEqualTo(EMPTY_SEARCH_POST_OPTION);

        // SearchKeyword가 null일 때
        // given & when
        EmptyValueException keywordException = assertThrows(EmptyValueException.class,
                () -> SearchPost.create(testSearchPostOption, null, testSearchPostTarget, SearchPostSortCondition.LATEST));

        // then
        assertThat(keywordException.getErrorCode()).isEqualTo(EMPTY_SEARCH_KEYWORD);

        // SearchPostTarget이 null일 때
        // given & when
        EmptyValueException targetException = assertThrows(EmptyValueException.class,
                () -> SearchPost.create(testSearchPostOption, testSearchKeyword, null, SearchPostSortCondition.LATEST));

        // then
        assertThat(targetException.getErrorCode()).isEqualTo(EMPTY_SEARCH_POST_TARGET);

        // SearchPostSortCondition이 null일 때
        // given & when
        EmptyValueException sortConditionException = assertThrows(EmptyValueException.class,
                () -> SearchPost.create(testSearchPostOption, testSearchKeyword, testSearchPostTarget, null));

        // then
        assertThat(sortConditionException.getErrorCode()).isEqualTo(EMPTY_SEARCH_KEYWORD_SIMILARITY);
    }

    @Test
    @DisplayName("정렬 조건이 LATEST일 때 중요도가 존재하면 예외 발생")
    void testCreate_givenLatestSortConditionWithImportance_willThrowException() {
        // given
        SearchPostOption searchPostOption = SearchPostOption.create(
                testSearchPostId, testSearchPostPublishedAt,
                SearchPostImportance.createEmpty(), SearchKeywordSimilarity.createEmpty());
        ReflectionTestUtils.setField(searchPostOption, "searchPostImportance", testSearchPostImportanceTitle);

        // when
        InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                SearchPost.create(searchPostOption, testSearchKeyword, testSearchPostTarget, SearchPostSortCondition.LATEST));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(INCORRECT_SEARCH_POST_OPTION);
    }

    @Test
    @DisplayName("정렬 조건이 LATEST일 때 유사도가 존재하면 예외 발생")
    void testCreate_givenLatestSortConditionWithSimilarity_willThrowException() {
        // given
        SearchPostOption searchPostOption = SearchPostOption.create(
                testSearchPostId, testSearchPostPublishedAt,
                testSearchPostImportanceTitle, testSearchKeywordSimilarity1);
        ReflectionTestUtils.setField(searchPostOption, "searchPostImportance", SearchPostImportance.createEmpty());

        // when
        InvalidValueException exception = assertThrows(InvalidValueException.class, () ->
                SearchPost.create(searchPostOption, testSearchKeyword, testSearchPostTarget, SearchPostSortCondition.LATEST));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(INCORRECT_SEARCH_POST_OPTION);
    }

    @Test
    @DisplayName("정렬 조건이 LATEST일 때 중요도와 유사도 값이 모두 비어있으면 정상 생성")
    void testCreate_givenLatestSortConditionWithValidOptions_willReturnSearchPost() {
        // given
        SearchPostOption searchPostOption = SearchPostOption.create(
                testSearchPostId, testSearchPostPublishedAt,
                SearchPostImportance.createEmpty(), SearchKeywordSimilarity.createEmpty());

        // when
        SearchPost searchPost = SearchPost.create(searchPostOption, testSearchKeyword, testSearchPostTarget, SearchPostSortCondition.LATEST);

        // then
        assertNotNull(searchPost);
        assertEquals(SearchPostSortCondition.LATEST, searchPost.getSearchPostSortCondition());
    }

    @Test
    @DisplayName("정렬 조건이 RELEVANCE일 때 커서 데이터가 일부만 존재하면 예외 발생")
    void testCreate_givenRelevanceSortConditionWithPartialOptions_willThrowException() {
        // given
        SearchPostOption searchPostOption = SearchPostOption.create(
                testSearchPostId, testSearchPostPublishedAt,
                SearchPostImportance.createEmpty(), SearchKeywordSimilarity.createEmpty());

        // when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> SearchPost.create(searchPostOption, testSearchKeyword, testSearchPostTarget, SearchPostSortCondition.RELEVANCE));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(INCORRECT_SEARCH_POST_OPTION);
    }

    @Test
    @DisplayName("정렬 조건이 RELEVANCE일 때 커서 데이터가 모두 없으면 정상 생성")
    void testCreate_givenRelevanceSortConditionWithNoCursorOptions_willReturnSearchPost() {
        // given
        SearchPostOption searchPostOption = SearchPostOption.create(
                testEmptySearchPostId, testEmptySearchPostPublishedAt,
                SearchPostImportance.createEmpty(), SearchKeywordSimilarity.createEmpty());

        // when
        SearchPost searchPost = SearchPost.create(searchPostOption, testSearchKeyword, testSearchPostTarget, SearchPostSortCondition.RELEVANCE);

        // then
        assertNotNull(searchPost);
        assertEquals(SearchPostSortCondition.RELEVANCE, searchPost.getSearchPostSortCondition());
    }

    @Test
    @DisplayName("정렬 조건이 RELEVANCE일 때 커서 데이터가 모두 존재하면 정상 생성")
    void testCreate_givenRelevanceSortConditionWithAllCursorOptions_willReturnSearchPost() {
        // given
        SearchPostOption searchPostOption = SearchPostOption.create(
                testSearchPostId, testSearchPostPublishedAt,
                testSearchPostImportanceTitle, testSearchKeywordSimilarity1);

        // when
        SearchPost searchPost = SearchPost.create(searchPostOption, testSearchKeyword, testSearchPostTarget, SearchPostSortCondition.RELEVANCE);

        // then
        assertNotNull(searchPost);
        assertEquals(SearchPostSortCondition.RELEVANCE, searchPost.getSearchPostSortCondition());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSearchPost, testSearchPost);
    }

    @Test
    @DisplayName("SearchPostId 값이 같은 다른 객체에 대한 equals 호출")
    void testEquals_givenDifferentObjectWithSameId_willReturnTrue() {
        // given
        SearchPost otherSearchPost = SearchPost.create(
                SearchPostOption.create(
                        SearchPostId.create(TEST_POST_ULID),
                        SearchPostPublishedAt.create(TEST_COMM_POST_PUBLISHED_AT),
                        SearchPostImportance.createEmpty(),
                        SearchKeywordSimilarity.createEmpty()),
                testSearchKeyword, testSearchPostTarget, SearchPostSortCondition.LATEST);

        // when & then
        assertEquals(testSearchPost, otherSearchPost);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSearchPost, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        // given
        SearchPost otherSearchPost = SearchPost.create(
                SearchPostOption.create(
                        SearchPostId.create(TEST_POST_ULID2),
                        SearchPostPublishedAt.create(TEST_COMM_POST_PUBLISHED_AT),
                        SearchPostImportance.createEmpty(),
                        SearchKeywordSimilarity.createEmpty()),
                testSearchKeyword, testSearchPostTarget, SearchPostSortCondition.LATEST);

        // when & then
        assertNotEquals(testSearchPost, otherSearchPost);
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSearchPost.hashCode(), testSearchPost.hashCode());
    }

    @Test
    @DisplayName("SearchPostId 값이 같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenDifferentObjectWithSameId_willReturnSameHashCode() {
        // given
        SearchPost otherSearchPost = SearchPost.create(
                SearchPostOption.create(
                        SearchPostId.create(TEST_POST_ULID),
                        SearchPostPublishedAt.create(TEST_COMM_POST_PUBLISHED_AT),
                        SearchPostImportance.createEmpty(),
                        SearchKeywordSimilarity.createEmpty()),
                testSearchKeyword, testSearchPostTarget, SearchPostSortCondition.LATEST);

        // when & then
        assertEquals(testSearchPost.hashCode(), otherSearchPost.hashCode());
    }
}