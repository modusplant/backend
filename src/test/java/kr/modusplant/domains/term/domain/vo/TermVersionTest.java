package kr.modusplant.domains.term.domain.vo;

import kr.modusplant.domains.term.domain.exception.EmptyTermVersionException;
import kr.modusplant.domains.term.domain.exception.InvalidTermVersionException;
import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.TermConstant.TEST_TERMS_OF_USE_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TermVersionTest {

    @Test
    @DisplayName("create로 TermVersion 반환")
    void testCreate_givenValidValue_willReturnTermVersion() {
        assertThat(TermVersion.create(TEST_TERMS_OF_USE_VERSION).getValue()).isEqualTo(TEST_TERMS_OF_USE_VERSION);
    }

    @Test
    @DisplayName("null로 create를 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyTermVersionException exception = assertThrows(EmptyTermVersionException.class, () -> TermVersion.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.EMPTY_TERM_VERSION);
    }

    @Test
    @DisplayName("빈 문자열로 create를 호출하여 오류 발생")
    void testCreate_givenBlankString_willThrowException() {
        EmptyTermVersionException exception = assertThrows(EmptyTermVersionException.class, () -> TermVersion.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.EMPTY_TERM_VERSION);
    }

    @Test
    @DisplayName("잘못된 형식으로 create를 호출하여 오류 발생")
    void testCreate_givenInvalidFormat_willThrowException() {
        InvalidTermVersionException exception = assertThrows(InvalidTermVersionException.class, () -> TermVersion.create("1.0.0"));
        assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.INVALID_TERM_VERSION);
    }

    @Test
    @DisplayName("compareTo로 버전 비교 (낮은 버전과 비교)")
    void testCompareTo_givenLowerVersion_willReturnPositive() {
        TermVersion v200 = TermVersion.create("v2.0.0");
        TermVersion v100 = TermVersion.create("v1.0.0");
        assertThat(v200.compareTo(v100)).isGreaterThan(0);
    }

    @Test
    @DisplayName("compareTo로 버전 비교 (높은 버전과 비교)")
    void testCompareTo_givenHigherVersion_willReturnNegative() {
        TermVersion v100 = TermVersion.create("v1.0.0");
        TermVersion v200 = TermVersion.create("v2.0.0");
        assertThat(v100.compareTo(v200)).isLessThan(0);
    }

    @Test
    @DisplayName("compareTo로 버전 비교 (같은 버전과 비교)")
    void testCompareTo_givenSameVersion_willReturnZero() {
        TermVersion v100a = TermVersion.create("v1.0.0");
        TermVersion v100b = TermVersion.create("v1.0.0");
        assertThat(v100a.compareTo(v100b)).isEqualTo(0);
    }

    @Test
    @DisplayName("같은 버전 값을 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingSameProperty_willReturnTrue() {
        assertEquals(TermVersion.create("v1.2.3"), TermVersion.create("v1.2.3"));
    }

    @Test
    @DisplayName("다른 버전 값을 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(TermVersion.create("v1.0.0"), TermVersion.create("v2.0.0"));
    }
}
