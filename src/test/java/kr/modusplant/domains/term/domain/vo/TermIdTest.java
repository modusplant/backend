package kr.modusplant.domains.term.domain.vo;

import kr.modusplant.domains.term.common.util.domain.aggregate.TermTestUtils;
import kr.modusplant.domains.term.domain.exception.EmptyTermIdException;
import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.term.common.util.domain.vo.TermIdTestUtils.testTermId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TermIdTest implements TermTestUtils {

    @Test
    @DisplayName("generate로 TermId 반환")
    void testGenerate_givenNoParameter_willReturnTermId() {
        assertNotNull(TermId.generate().getValue());
    }

    @Test
    @DisplayName("fromUuid로 TermId 반환")
    void testFromUuid_givenValidValue_willReturnTermId() {
        assertNotNull(TermId.fromUuid(UUID.randomUUID()).getValue());
    }

    @Test
    @DisplayName("null로 fromUuid를 호출하여 오류 발생")
    void testFromUuid_givenNull_willThrowException() {
        EmptyTermIdException exception = assertThrows(EmptyTermIdException.class, () -> TermId.fromUuid(null));
        assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.EMPTY_TERM_ID);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testTermId, testTermId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testTermId, "notATermId");
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testTermId, TermId.generate());
    }
}
