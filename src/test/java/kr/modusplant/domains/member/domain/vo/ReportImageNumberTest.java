package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageNumberTestUtils.testReportImageNumber3;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_IMAGE_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReportImageNumberTest {
    @Test
    @DisplayName("create으로 보고서 이미지 개수 반환")
    void testCreate_givenValidValue_willReturnReportImageNumber() {
        assertThat(ReportImageNumber.create(TEST_REPORT_IMAGE_NUMBER)).isEqualTo(ReportImageNumber.create(TEST_REPORT_IMAGE_NUMBER));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ReportImageNumber.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_IMAGE_NUMBER);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testReportImageNumber3, testReportImageNumber3);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testReportImageNumber3, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testReportImageNumber3, ReportImageNumber.create(TEST_REPORT_IMAGE_NUMBER + 1));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testReportImageNumber3.hashCode(), testReportImageNumber3.hashCode());
    }
}