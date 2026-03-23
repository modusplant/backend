package kr.modusplant.domains.term.domain.vo;

import kr.modusplant.domains.term.domain.exception.EmptyTermContentException;
import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.TermConstant.TEST_TERMS_OF_USE_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TermContentTest {

    @Test
    @DisplayName("create로 TermContent 반환")
    void testCreate_givenValidValue_willReturnTermContent() {
        assertThat(TermContent.create(TEST_TERMS_OF_USE_CONTENT).getValue()).isEqualTo(TEST_TERMS_OF_USE_CONTENT);
    }

    @Test
    @DisplayName("null로 create를 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyTermContentException exception = assertThrows(EmptyTermContentException.class, () -> TermContent.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.EMPTY_TERM_CONTENT);
    }

    @Test
    @DisplayName("빈 문자열로 create를 호출하여 오류 발생")
    void testCreate_givenBlankString_willThrowException() {
        EmptyTermContentException exception = assertThrows(EmptyTermContentException.class, () -> TermContent.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.EMPTY_TERM_CONTENT);
    }

    @Test
    @DisplayName("같은 값을 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingSameProperty_willReturnTrue() {
        assertEquals(TermContent.create(TEST_TERMS_OF_USE_CONTENT), TermContent.create(TEST_TERMS_OF_USE_CONTENT));
    }

    @Test
    @DisplayName("다른 값을 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(TermContent.create(TEST_TERMS_OF_USE_CONTENT), TermContent.create("다른 내용"));
    }
}
