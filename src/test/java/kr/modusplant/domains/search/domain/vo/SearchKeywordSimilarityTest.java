package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.TEST_SEARCH_KEYWORD_SIMILARITY_0_6;
import static kr.modusplant.domains.search.common.constant.SearchDoubleConstant.TEST_SEARCH_KEYWORD_SIMILARITY_0_8;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordSimilarityTestUtils.testSearchKeywordSimilarity06;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordSimilarityTestUtils.testSearchKeywordSimilarity08;
import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearchKeywordSimilarityTest {

    @Test
    @DisplayName("유효한 값으로 create 호출 시 정상 객체 반환")
    void testCreate_givenValidValue_willReturnSearchKeywordSimilarity() {
        assertNotNull(testSearchKeywordSimilarity06);
        assertFalse(testSearchKeywordSimilarity06.isEmpty());
        assertEquals(TEST_SEARCH_KEYWORD_SIMILARITY_0_6, testSearchKeywordSimilarity06.getValueIfNotEmpty());
    }

    @Test
    @DisplayName("null 값으로 create 호출 시 예외 발생")
    void testCreate_givenNull_willThrowException() {
        // given & when
        EmptyValueException exception = assertThrows(EmptyValueException.class,
                () -> SearchKeywordSimilarity.create(null));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(EMPTY_SEARCH_KEYWORD_SIMILARITY);
    }

    @Test
    @DisplayName("0보다 작은 값으로 create 호출 시 예외 발생")
    void testCreate_givenValueLessThanZero_willThrowException() {
        // given & when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> SearchKeywordSimilarity.create(-0.1));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SEARCH_KEYWORD_SIMILARITY_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("1보다 큰 값으로 create 호출 시 예외 발생")
    void testCreate_givenValueGreaterThanOne_willThrowException() {
        // given & when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> SearchKeywordSimilarity.create(1.1));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SEARCH_KEYWORD_SIMILARITY_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("경계값(0과 1)으로 create 호출 시 정상 객체 반환")
    void testCreate_givenBoundaryValues_willReturnSearchKeywordSimilarity() {
        // given & when
        SearchKeywordSimilarity minSimilarity = SearchKeywordSimilarity.create(0.0);
        SearchKeywordSimilarity maxSimilarity = SearchKeywordSimilarity.create(1.0);

        // then
        assertEquals(0.0, minSimilarity.getValueIfNotEmpty());
        assertEquals(1.0, maxSimilarity.getValueIfNotEmpty());
    }

    @Test
    @DisplayName("createEmpty 호출 시 비어있는 객체 반환")
    void testCreateEmpty_givenNothing_willReturnEmptyInstance() {
        // given & when
        SearchKeywordSimilarity emptySimilarity = SearchKeywordSimilarity.createEmpty();

        // then
        assertTrue(emptySimilarity.isEmpty());
    }

    @Test
    @DisplayName("빈 객체에 대해 getValueIfNotEmpty 호출 시 예외 발생")
    void testGetValueIfNotEmpty_givenEmptyObject_willThrowException() {
        // given
        SearchKeywordSimilarity emptySimilarity = SearchKeywordSimilarity.createEmpty();

        // when
        InvalidValueException exception = assertThrows(InvalidValueException.class, emptySimilarity::getValueIfNotEmpty);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(NOT_FOUND_SEARCH_POST_IMPORTANCE);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSearchKeywordSimilarity08, testSearchKeywordSimilarity08);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSearchKeywordSimilarity08, testMemberId);
    }

    @Test
    @DisplayName("값이 같은 다른 객체에 대한 equals 호출")
    void testEquals_givenDifferentObjectWithSameValue_willReturnTrue() {
        // given
        SearchKeywordSimilarity otherSimilarity = SearchKeywordSimilarity.create(TEST_SEARCH_KEYWORD_SIMILARITY_0_8);

        // when & then
        assertEquals(testSearchKeywordSimilarity08, otherSimilarity);
    }

    @Test
    @DisplayName("다른 프로퍼티(값)를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testSearchKeywordSimilarity06, testSearchKeywordSimilarity08);
    }

    @Test
    @DisplayName("두 객체가 모두 빈 객체일 때 equals 호출")
    void testEquals_givenBothEmptyObjects_willReturnTrue() {
        // given
        SearchKeywordSimilarity empty1 = SearchKeywordSimilarity.createEmpty();
        SearchKeywordSimilarity empty2 = SearchKeywordSimilarity.createEmpty();

        // when & then
        assertEquals(empty1, empty2);
    }

    @Test
    @DisplayName("하나만 빈 객체일 때 equals 호출")
    void testEquals_givenOneEmptyObject_willReturnFalse() {
        // given
        SearchKeywordSimilarity empty = SearchKeywordSimilarity.createEmpty();

        // when & then
        assertNotEquals(empty, testSearchKeywordSimilarity06);
        assertNotEquals(testSearchKeywordSimilarity06, empty);
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSearchKeywordSimilarity08.hashCode(), testSearchKeywordSimilarity08.hashCode());
    }

    @Test
    @DisplayName("값이 같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenDifferentObjectWithSameValue_willReturnSameHashCode() {
        // given
        SearchKeywordSimilarity otherSimilarity = SearchKeywordSimilarity.create(TEST_SEARCH_KEYWORD_SIMILARITY_0_6);

        // when & then
        assertEquals(testSearchKeywordSimilarity06.hashCode(), otherSimilarity.hashCode());
    }

    @Test
    @DisplayName("빈 객체의 hashCode는 0 반환")
    void testHashCode_givenEmptyObject_willReturnZero() {
        // given
        SearchKeywordSimilarity emptySimilarity = SearchKeywordSimilarity.createEmpty();

        // when & then
        assertEquals(0, emptySimilarity.hashCode());
    }
}