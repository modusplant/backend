package kr.modusplant.domains.search.domain.entity;

import kr.modusplant.domains.search.domain.vo.SearchKeywordSimilarity;
import kr.modusplant.domains.search.domain.vo.SearchPostId;
import kr.modusplant.domains.search.domain.vo.SearchPostImportance;
import kr.modusplant.domains.search.domain.vo.SearchPostPublishedAt;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID2;
import static kr.modusplant.domains.search.common.util.domain.entity.SearchPostOptionTestUtils.testSearchPostOption;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordSimilarityTestUtils.testSearchKeywordSimilarity1;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostIdTestUtils.testSearchPostId;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostImportanceTestUtils.testSearchPostImportanceTitle;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostPublishedAtTestUtils.testSearchPostPublishedAt;
import static kr.modusplant.domains.search.common.util.domain.vo.nullobject.EmptySearchPostIdTestUtils.testEmptySearchPostId;
import static kr.modusplant.domains.search.common.util.domain.vo.nullobject.EmptySearchPostPublishedAtTestUtils.testEmptySearchPostPublishedAt;
import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.*;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_PUBLISHED_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearchPostOptionTest {

    @Test
    @DisplayName("null 값으로 create 호출 시 예외 발생")
    void testCreate_givenNullToOneOfFourParameters_willThrowException() {
        // SearchPostId가 null일 때
        // given & when
        EmptyValueException idException = assertThrows(EmptyValueException.class,
                () -> SearchPostOption.create(null, testSearchPostPublishedAt, testSearchPostImportanceTitle, testSearchKeywordSimilarity1));

        // then
        assertThat(idException.getErrorCode()).isEqualTo(EMPTY_SEARCH_POST_ID);

        // SearchPostPublishedAt이 null일 때
        // given & when
        EmptyValueException publishedAtException = assertThrows(EmptyValueException.class,
                () -> SearchPostOption.create(testSearchPostId, null, testSearchPostImportanceTitle, testSearchKeywordSimilarity1));

        // then
        assertThat(publishedAtException.getErrorCode()).isEqualTo(EMPTY_SEARCH_POST_PUBLISHED_AT);

        // SearchPostImportance가 null일 때
        // given & when
        EmptyValueException importanceException = assertThrows(EmptyValueException.class,
                () -> SearchPostOption.create(testSearchPostId, testSearchPostPublishedAt, null, testSearchKeywordSimilarity1));

        // then
        assertThat(importanceException.getErrorCode()).isEqualTo(EMPTY_SEARCH_POST_IMPORTANCE);

        // SearchKeywordSimilarity가 null일 때
        // given & when
        EmptyValueException similarityException = assertThrows(EmptyValueException.class,
                () -> SearchPostOption.create(testSearchPostId, testSearchPostPublishedAt, testSearchPostImportanceTitle, null));

        // then
        assertThat(similarityException.getErrorCode()).isEqualTo(EMPTY_SEARCH_KEYWORD_SIMILARITY);
    }

    @Test
    @DisplayName("ID는 비어있으나 발행일이 존재할 경우 예외 발생")
    void testCreate_givenEmptyIdAndValidPublishedAt_willThrowException() {
        // given & when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> SearchPostOption.create(testEmptySearchPostId, testSearchPostPublishedAt, SearchPostImportance.createEmpty(), SearchKeywordSimilarity.createEmpty()));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INVALID_INPUT);
    }

    @Test
    @DisplayName("ID는 존재하나 발행일이 비어있을 경우 예외 발생")
    void testCreate_givenValidIdAndEmptyPublishedAt_willThrowException() {
        // given & when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> SearchPostOption.create(testSearchPostId, testEmptySearchPostPublishedAt, SearchPostImportance.createEmpty(), SearchKeywordSimilarity.createEmpty()));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INVALID_INPUT);
    }

    @Test
    @DisplayName("중요도는 비어있으나 유사도가 존재할 경우 예외 발생")
    void testCreate_givenEmptyImportanceAndValidSimilarity_willThrowException() {
        // given & when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> SearchPostOption.create(testSearchPostId, testSearchPostPublishedAt, SearchPostImportance.createEmpty(), testSearchKeywordSimilarity1));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INVALID_INPUT);
    }

    @Test
    @DisplayName("중요도는 존재하나 유사도가 비어있을 경우 예외 발생")
    void testCreate_givenValidImportanceAndEmptySimilarity_willThrowException() {
        // given & when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> SearchPostOption.create(testSearchPostId, testSearchPostPublishedAt, testSearchPostImportanceTitle, SearchKeywordSimilarity.createEmpty()));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INVALID_INPUT);
    }

    @Test
    @DisplayName("모든 조건이 올바르게 채워졌을 때 정상 생성 (모두 값이 있는 경우)")
    void testCreate_givenAllValidOptions_willReturnSearchPostOption() {
        // given & when
        SearchPostOption option = SearchPostOption.create(testSearchPostId, testSearchPostPublishedAt, testSearchPostImportanceTitle, testSearchKeywordSimilarity1);

        // then
        assertNotNull(option);
        assertEquals(testSearchPostId, option.getSearchPostId());
    }

    @Test
    @DisplayName("모든 조건이 올바르게 채워졌을 때 정상 생성 (기본 ID, 발행일만 있는 경우)")
    void testCreate_givenValidBaseOptionsAndEmptyCursorOptions_willReturnSearchPostOption() {
        // given & when
        SearchPostOption option = SearchPostOption.create(testSearchPostId, testSearchPostPublishedAt, SearchPostImportance.createEmpty(), SearchKeywordSimilarity.createEmpty());

        // then
        assertNotNull(option);
        assertEquals(testSearchPostId, option.getSearchPostId());
    }

    @Test
    @DisplayName("모든 조건이 올바르게 채워졌을 때 정상 생성 (모두 비어있는 경우)")
    void testCreate_givenAllEmptyOptions_willReturnSearchPostOption() {
        // given & when
        SearchPostOption option = SearchPostOption.create(testEmptySearchPostId, testEmptySearchPostPublishedAt, SearchPostImportance.createEmpty(), SearchKeywordSimilarity.createEmpty());

        // then
        assertNotNull(option);
        assertEquals(testEmptySearchPostId, option.getSearchPostId());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSearchPostOption, testSearchPostOption);
    }

    @Test
    @DisplayName("SearchPostId 값이 같은 다른 객체에 대한 equals 호출")
    void testEquals_givenDifferentObjectWithSameId_willReturnTrue() {
        // given
        SearchPostOption otherOption = SearchPostOption.create(
                SearchPostId.create(TEST_POST_ULID),
                SearchPostPublishedAt.create(TEST_COMM_POST_PUBLISHED_AT),
                SearchPostImportance.createEmpty(),
                SearchKeywordSimilarity.createEmpty()
        );

        // when & then
        assertEquals(testSearchPostOption, otherOption);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSearchPostOption, testMemberId);
    }

    @Test
    @DisplayName("다른 SearchPostId를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentId_willReturnFalse() {
        // given
        SearchPostOption otherOption = SearchPostOption.create(
                SearchPostId.create(TEST_POST_ULID2),
                SearchPostPublishedAt.create(TEST_COMM_POST_PUBLISHED_AT),
                SearchPostImportance.createEmpty(),
                SearchKeywordSimilarity.createEmpty()
        );

        // when & then
        assertNotEquals(testSearchPostOption, otherOption);
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSearchPostOption.hashCode(), testSearchPostOption.hashCode());
    }

    @Test
    @DisplayName("SearchPostId 값이 같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenDifferentObjectWithSameId_willReturnSameHashCode() {
        // given
        SearchPostOption otherOption = SearchPostOption.create(
                SearchPostId.create(TEST_POST_ULID),
                SearchPostPublishedAt.create(TEST_COMM_POST_PUBLISHED_AT),
                SearchPostImportance.createEmpty(),
                SearchKeywordSimilarity.createEmpty()
        );

        // when & then
        assertEquals(testSearchPostOption.hashCode(), otherOption.hashCode());
    }
}