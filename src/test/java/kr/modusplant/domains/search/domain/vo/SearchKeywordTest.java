package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.search.common.constant.SearchStringConstant.TEST_SEARCH_KEYWORD;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchKeywordTestUtils.testSearchKeyword;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearchKeywordTest {
    @Test
    @DisplayName("create으로 보고서 제목 반환")
    void testCreate_givenValidValue_willReturnSearchKeyword() {
        assertThat(SearchKeyword.create(TEST_SEARCH_KEYWORD)).isEqualTo(SearchKeyword.create(TEST_SEARCH_KEYWORD));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> SearchKeyword.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(SearchErrorCode.EMPTY_SEARCH_KEYWORD);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> SearchKeyword.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(SearchErrorCode.EMPTY_SEARCH_KEYWORD);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSearchKeyword, testSearchKeyword);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSearchKeyword, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testSearchKeyword, SearchKeyword.create(String.valueOf(UUID.randomUUID())));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testSearchKeyword.hashCode(), testSearchKeyword.hashCode());
    }
}