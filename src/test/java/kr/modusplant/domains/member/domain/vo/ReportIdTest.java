package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReportIdTest {
    @Test
    @DisplayName("create으로 보고서 식별자 반환")
    void testCreate_givenValidValue_willReturnReportId() {
        assertThat(ReportId.create(TEST_REPORT_ULID)).isEqualTo(ReportId.create(TEST_REPORT_ULID));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ReportId.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_ID);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ReportId.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_ID);
    }

    @Test
    @DisplayName("유효하지 않은 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenInvalidString_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> ReportId.create("InvalidData"));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.INVALID_REPORT_ID);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testReportId, testReportId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testReportId, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testReportId, ReportId.create(
                new UlidIdGenerator().generate(null, null, null, EventType.INSERT)));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testReportId.hashCode(), testReportId.hashCode());
    }
}