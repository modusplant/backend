package kr.modusplant.domains.search.domain.entity;

import kr.modusplant.domains.search.domain.vo.SearchKeywordSimilarity;
import kr.modusplant.domains.search.domain.vo.SearchPostId;
import kr.modusplant.domains.search.domain.vo.SearchPostImportance;
import kr.modusplant.domains.search.domain.vo.SearchPostPublishedAt;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.post.common.constant.PostConstant.*;
import static kr.modusplant.domains.search.common.util.domain.entity.SearchPostOptionTestUtils.testSearchPostOption;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordSimilarityTestUtils.testSearchKeywordSimilarity1;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostIdTestUtils.testSearchPostId;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostImportanceTestUtils.testSearchPostImportanceTitle;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostPublishedAtTestUtils.testSearchPostPublishedAt;
import static kr.modusplant.domains.search.common.util.domain.vo.nullobject.EmptySearchPostIdTestUtils.testEmptySearchPostId;
import static kr.modusplant.domains.search.common.util.domain.vo.nullobject.EmptySearchPostPublishedAtTestUtils.testEmptySearchPostPublishedAt;
import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearchPostOptionTest {
    @Nested
    @DisplayName("createRelevanceOption 테스트")
    class CreateRelevanceOptionTest {
        @Test
        @DisplayName("SearchPostId가 null일 때 예외 반환")
        void testCreateRelevanceOption_givenNullSearchPostId_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> SearchPostOption.createRelevanceOption(null, testSearchPostPublishedAt, testSearchPostImportanceTitle, testSearchKeywordSimilarity1));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_SEARCH_POST_ID);
        }

        @Test
        @DisplayName("SearchPostPublishedAt이 null일 때 예외 반환")
        void testCreateRelevanceOption_givenNullSearchPostPublishedAt_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> SearchPostOption.createRelevanceOption(testSearchPostId, null, testSearchPostImportanceTitle, testSearchKeywordSimilarity1));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_SEARCH_POST_PUBLISHED_AT);
        }

        @Test
        @DisplayName("SearchPostImportance가 null일 때 예외 반환")
        void testCreateRelevanceOption_givenNullSearchPostImportance_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> SearchPostOption.createRelevanceOption(testSearchPostId, testSearchPostPublishedAt, null, testSearchKeywordSimilarity1));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_SEARCH_POST_IMPORTANCE);
        }

        @Test
        @DisplayName("SearchKeywordSimilarity가 null일 때 예외 반환")
        void testCreateRelevanceOption_givenNullSearchKeywordSimilarity_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> SearchPostOption.createRelevanceOption(testSearchPostId, testSearchPostPublishedAt, testSearchPostImportanceTitle, null));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_SEARCH_KEYWORD_SIMILARITY);
        }

        @Test
        @DisplayName("ID는 비어있으나 발행일이 존재할 때 예외 반환")
        void testCreateRelevanceOption_givenEmptyIdAndValidPublishedAt_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class,
                    () -> SearchPostOption.createRelevanceOption(testEmptySearchPostId, testSearchPostPublishedAt, SearchPostImportance.empty(), SearchKeywordSimilarity.createEmpty()));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INVALID_INPUT);
        }

        @Test
        @DisplayName("ID는 존재하나 발행일이 비어있을 때 예외 반환")
        void testCreateRelevanceOption_givenValidIdAndEmptyPublishedAt_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class,
                    () -> SearchPostOption.createRelevanceOption(testSearchPostId, testEmptySearchPostPublishedAt, SearchPostImportance.empty(), SearchKeywordSimilarity.createEmpty()));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INVALID_INPUT);
        }

        @Test
        @DisplayName("중요도는 비어있으나 유사도가 존재할 때 예외 반환")
        void testCreateRelevanceOption_givenEmptyImportanceAndValidSimilarity_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class,
                    () -> SearchPostOption.createRelevanceOption(testSearchPostId, testSearchPostPublishedAt, SearchPostImportance.empty(), testSearchKeywordSimilarity1));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INVALID_INPUT);
        }

        @Test
        @DisplayName("중요도는 존재하나 유사도가 비어있을 때 예외 반환")
        void testCreateRelevanceOption_givenValidImportanceAndEmptySimilarity_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class,
                    () -> SearchPostOption.createRelevanceOption(testSearchPostId, testSearchPostPublishedAt, testSearchPostImportanceTitle, SearchKeywordSimilarity.createEmpty()));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INVALID_INPUT);
        }

        @Test
        @DisplayName("모든 옵션에 값이 있을 때 SearchPostOption 반환")
        void testCreateRelevanceOption_givenAllValidOptions_willReturnSearchPostOption() {
            // given & when
            SearchPostOption option = SearchPostOption.createRelevanceOption(testSearchPostId, testSearchPostPublishedAt, testSearchPostImportanceTitle, testSearchKeywordSimilarity1);

            // then
            assertNotNull(option);
            assertEquals(testSearchPostId, option.getSearchPostId());
        }

        @Test
        @DisplayName("기본 옵션은 유효하고 커서 옵션은 비어있을 때 SearchPostOption 반환")
        void testCreateRelevanceOption_givenValidBaseOptionsAndEmptyCursorOptions_willReturnSearchPostOption() {
            // given & when
            SearchPostOption option = SearchPostOption.createRelevanceOption(testSearchPostId, testSearchPostPublishedAt, SearchPostImportance.empty(), SearchKeywordSimilarity.createEmpty());

            // then
            assertNotNull(option);
            assertEquals(testSearchPostId, option.getSearchPostId());
        }

        @Test
        @DisplayName("모든 옵션이 비어있을 때 SearchPostOption 반환")
        void testCreateRelevanceOption_givenAllEmptyOptions_willReturnSearchPostOption() {
            // given & when
            SearchPostOption option = SearchPostOption.createRelevanceOption(testEmptySearchPostId, testEmptySearchPostPublishedAt, SearchPostImportance.empty(), SearchKeywordSimilarity.createEmpty());

            // then
            assertNotNull(option);
            assertEquals(testEmptySearchPostId, option.getSearchPostId());
        }
    }

    @Nested
    @DisplayName("createLatestOption 테스트")
    class CreateLatestOptionTest {
        @Test
        @DisplayName("SearchPostId가 null일 때 예외 반환")
        void testCreateLatestOption_givenNullSearchPostId_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> SearchPostOption.createLatestOption(null, testSearchPostPublishedAt));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_SEARCH_POST_ID);
        }

        @Test
        @DisplayName("SearchPostPublishedAt이 null일 때 예외 반환")
        void testCreateLatestOption_givenNullSearchPostPublishedAt_willThrowException() {
            // given & when
            EmptyValueException exception = assertThrows(EmptyValueException.class,
                    () -> SearchPostOption.createLatestOption(testSearchPostId, null));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(EMPTY_SEARCH_POST_PUBLISHED_AT);
        }

        @Test
        @DisplayName("ID는 비어있으나 발행일이 존재할 때 예외 반환")
        void testCreateLatestOption_givenEmptyIdAndValidPublishedAt_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class,
                    () -> SearchPostOption.createLatestOption(testEmptySearchPostId, testSearchPostPublishedAt));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INVALID_INPUT);
        }

        @Test
        @DisplayName("ID는 존재하나 발행일이 비어있을 때 예외 반환")
        void testCreateLatestOption_givenValidIdAndEmptyPublishedAt_willThrowException() {
            // given & when
            InvalidValueException exception = assertThrows(InvalidValueException.class,
                    () -> SearchPostOption.createLatestOption(testSearchPostId, testEmptySearchPostPublishedAt));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INVALID_INPUT);
        }

        @Test
        @DisplayName("모든 옵션에 값이 있을 때 SearchPostOption 반환")
        void testCreateLatestOption_givenAllValidOptions_willReturnSearchPostOption() {
            // given & when
            SearchPostOption option = SearchPostOption.createLatestOption(testSearchPostId, testSearchPostPublishedAt);

            // then
            assertNotNull(option);
            assertEquals(testSearchPostId, option.getSearchPostId());
        }

        @Test
        @DisplayName("모든 옵션이 비어있을 때 SearchPostOption 반환")
        void testCreateLatestOption_givenAllEmptyOptions_willReturnSearchPostOption() {
            // given & when
            SearchPostOption option = SearchPostOption.createLatestOption(testEmptySearchPostId, testEmptySearchPostPublishedAt);

            // then
            assertNotNull(option);
            assertEquals(testEmptySearchPostId, option.getSearchPostId());
        }
    }

    @Test
    @DisplayName("같은 객체로 참 반환")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSearchPostOption, testSearchPostOption);
    }

    @Test
    @DisplayName("SearchPostId가 같은 다른 객체로 참 반환")
    void testEquals_givenDifferentObjectWithSameId_willReturnTrue() {
        // given
        SearchPostOption otherOption = SearchPostOption.createRelevanceOption(
                SearchPostId.create(TEST_POST_ULID),
                SearchPostPublishedAt.create(TEST_POST_PUBLISHED_AT),
                SearchPostImportance.empty(),
                SearchKeywordSimilarity.createEmpty()
        );

        // when & then
        assertEquals(testSearchPostOption, otherOption);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스로 거짓 반환")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSearchPostOption, testMemberId);
    }

    @Test
    @DisplayName("다른 SearchPostId를 갖는 인스턴스로 거짓 반환")
    void testEquals_givenObjectContainingDifferentId_willReturnFalse() {
        // given
        SearchPostOption otherOption = SearchPostOption.createRelevanceOption(
                SearchPostId.create(TEST_POST_ULID2),
                SearchPostPublishedAt.create(TEST_POST_PUBLISHED_AT),
                SearchPostImportance.empty(),
                SearchKeywordSimilarity.createEmpty()
        );

        // when & then
        assertNotEquals(testSearchPostOption, otherOption);
    }

    @Test
    @DisplayName("같은 객체로 동일한 해시코드 반환")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSearchPostOption.hashCode(), testSearchPostOption.hashCode());
    }

    @Test
    @DisplayName("SearchPostId가 같은 객체로 동일한 해시코드 반환")
    void testHashCode_givenDifferentObjectWithSameId_willReturnSameHashCode() {
        // given
        SearchPostOption otherOption = SearchPostOption.createRelevanceOption(
                SearchPostId.create(TEST_POST_ULID),
                SearchPostPublishedAt.create(TEST_POST_PUBLISHED_AT),
                SearchPostImportance.empty(),
                SearchKeywordSimilarity.createEmpty()
        );

        // when & then
        assertEquals(testSearchPostOption.hashCode(), otherOption.hashCode());
    }
}
