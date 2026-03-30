package kr.modusplant.domains.term.domain.vo;

import kr.modusplant.domains.term.domain.exception.EmptyTermNameException;
import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.TermConstant.TEST_TERMS_OF_USE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TermNameTest {

    @Test
    @DisplayName("create로 TermName 반환")
    void testCreate_givenValidValue_willReturnTermName() {
        assertThat(TermName.create(TEST_TERMS_OF_USE_NAME).getValue()).isEqualTo(TEST_TERMS_OF_USE_NAME);
    }

    @Test
    @DisplayName("null로 create를 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyTermNameException exception = assertThrows(EmptyTermNameException.class, () -> TermName.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.EMPTY_TERM_NAME);
    }

    @Test
    @DisplayName("빈 문자열로 create를 호출하여 오류 발생")
    void testCreate_givenBlankString_willThrowException() {
        EmptyTermNameException exception = assertThrows(EmptyTermNameException.class, () -> TermName.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.EMPTY_TERM_NAME);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        TermName termName = TermName.create(TEST_TERMS_OF_USE_NAME);
        //noinspection EqualsWithItself
        assertEquals(termName, termName);
    }

    @Test
    @DisplayName("같은 값을 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingSameProperty_willReturnTrue() {
        assertEquals(TermName.create(TEST_TERMS_OF_USE_NAME), TermName.create(TEST_TERMS_OF_USE_NAME));
    }

    @Test
    @DisplayName("다른 값을 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(TermName.create(TEST_TERMS_OF_USE_NAME), TermName.create("개인정보처리방침"));
    }
}
