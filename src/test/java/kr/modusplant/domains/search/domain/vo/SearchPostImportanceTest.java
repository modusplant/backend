package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostImportanceTestUtils.*;
import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearchPostImportanceTest {

    @Test
    @DisplayName("유효한 값(1~4)으로 create 호출 시 정상 객체 반환")
    void testCreate_givenValidValues_willReturnSearchPostImportance() {
        // given & when & then
        assertFalse(testSearchPostImportanceTitle.isEmpty());
        assertEquals((Integer) SearchPostImportance.title().getValueIfNotEmpty(), testSearchPostImportanceTitle.getValueIfNotEmpty());

        assertFalse(testSearchPostImportanceContent.isEmpty());
        assertEquals((Integer) SearchPostImportance.content().getValueIfNotEmpty(), testSearchPostImportanceContent.getValueIfNotEmpty());

        assertFalse(testSearchPostImportanceCommentContent.isEmpty());
        assertEquals((Integer) SearchPostImportance.commentContent().getValueIfNotEmpty(), testSearchPostImportanceCommentContent.getValueIfNotEmpty());

        assertFalse(testSearchPostImportanceOthers.isEmpty());
        assertEquals((Integer) SearchPostImportance.others().getValueIfNotEmpty(), testSearchPostImportanceOthers.getValueIfNotEmpty());
    }

    @Test
    @DisplayName("null 값으로 create 호출 시 예외 발생")
    void testCreate_givenNull_willThrowException() {
        // given & when
        EmptyValueException exception = assertThrows(EmptyValueException.class,
                () -> SearchPostImportance.create(null));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(EMPTY_SEARCH_POST_IMPORTANCE);
    }

    @Test
    @DisplayName("1보다 작은 값으로 create 호출 시 예외 발생")
    void testCreate_givenValueLessThanOne_willThrowException() {
        // given & when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> SearchPostImportance.create(0));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SEARCH_POST_IMPORTANCE_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("4보다 큰 값으로 create 호출 시 예외 발생")
    void testCreate_givenValueGreaterThanFour_willThrowException() {
        // given & when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> SearchPostImportance.create(5));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(SEARCH_POST_IMPORTANCE_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("createEmpty 호출 시 비어있는 객체 반환")
    void testEmptyInstance() {
        // given & when
        SearchPostImportance emptyImportance = SearchPostImportance.empty();

        // then
        assertTrue(emptyImportance.isEmpty());
    }

    @Test
    @DisplayName("빈 객체에 대해 getValueIfNotEmpty 호출 시 예외 발생")
    void testGetValueIfNotEmpty_givenEmptyObject_willThrowException() {
        // given
        SearchPostImportance emptyImportance = SearchPostImportance.empty();

        // when
        InvalidValueException exception = assertThrows(InvalidValueException.class, emptyImportance::getValueIfNotEmpty);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(NOT_FOUND_SEARCH_POST_IMPORTANCE);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSearchPostImportanceTitle, testSearchPostImportanceTitle);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSearchPostImportanceTitle, testMemberId);
    }

    @Test
    @DisplayName("값이 같은 다른 객체에 대한 equals 호출")
    void testEquals_givenDifferentObjectWithSameValue_willReturnTrue() {
        // given
        SearchPostImportance sameImportance = SearchPostImportance.create(SearchPostImportance.title().getValueIfNotEmpty());

        // when & then
        assertEquals(testSearchPostImportanceTitle, sameImportance);
    }

    @Test
    @DisplayName("다른 프로퍼티(값)를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        // given, when & then
        assertNotEquals(testSearchPostImportanceTitle, testSearchPostImportanceContent);
    }

    @Test
    @DisplayName("두 객체가 모두 빈 객체일 때 equals 호출")
    void testEquals_givenBothEmptyObjects_willReturnTrue() {
        // given
        SearchPostImportance empty1 = SearchPostImportance.empty();
        SearchPostImportance empty2 = SearchPostImportance.empty();

        // when & then
        assertEquals(empty1, empty2);
    }

    @Test
    @DisplayName("하나만 빈 객체일 때 equals 호출")
    void testEquals_givenOneEmptyObject_willReturnFalse() {
        // given
        SearchPostImportance empty = SearchPostImportance.empty();

        // when & then
        assertNotEquals(empty, testSearchPostImportanceTitle);
        assertNotEquals(testSearchPostImportanceTitle, empty);
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSearchPostImportanceTitle.hashCode(), testSearchPostImportanceTitle.hashCode());
    }

    @Test
    @DisplayName("값이 같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenDifferentObjectWithSameValue_willReturnSameHashCode() {
        // given
        SearchPostImportance sameImportance = SearchPostImportance.create(SearchPostImportance.title().getValueIfNotEmpty());

        // when & then
        assertEquals(testSearchPostImportanceTitle.hashCode(), sameImportance.hashCode());
    }

    @Test
    @DisplayName("빈 객체의 hashCode는 0 반환")
    void testHashCode_givenEmptyObject_willReturnZero() {
        // given
        SearchPostImportance emptyImportance = SearchPostImportance.empty();

        // when & then
        assertEquals(0, emptyImportance.hashCode());
    }
}