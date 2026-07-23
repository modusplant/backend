package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordTestUtils.testSearchKeyword;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchResultListSizeTestUtils.testSearchResultListSize;
import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.EMPTY_SEARCH_RESULT_LIST_SIZE;
import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.SEARCH_RESULT_LIST_SIZE_OUT_OF_RANGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearchResultListSizeTest {
    @Test
    @DisplayName("create으로 검색 결과 목록 크기 반환")
    void testCreate_givenValidValue_willReturnSearchResultListSize() {
        assertThat(SearchResultListSize.create(1).getValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("경계값으로 create을 호출하여 검색 결과 목록 크기 반환")
    void testCreate_givenBoundaryValues_willReturnSearchResultListSize() {
        assertThat(SearchResultListSize.create(1).getValue()).isEqualTo(1);
        assertThat(SearchResultListSize.create(50).getValue()).isEqualTo(50);
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> SearchResultListSize.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(EMPTY_SEARCH_RESULT_LIST_SIZE);
    }

    @Test
    @DisplayName("0 이하의 값으로 create을 호출하여 오류 발생")
    void testCreate_givenValueLessThanOrEqualToZero_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> SearchResultListSize.create(0));
        assertThat(exception.getErrorCode()).isEqualTo(SEARCH_RESULT_LIST_SIZE_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("50을 초과하는 값으로 create을 호출하여 오류 발생")
    void testCreate_givenValueGreaterThanFifty_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> SearchResultListSize.create(51));
        assertThat(exception.getErrorCode()).isEqualTo(SEARCH_RESULT_LIST_SIZE_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSearchResultListSize, testSearchResultListSize);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSearchResultListSize, testSearchKeyword);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testSearchResultListSize, SearchResultListSize.create(2));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSearchResultListSize.hashCode(), testSearchResultListSize.hashCode());
    }
}
