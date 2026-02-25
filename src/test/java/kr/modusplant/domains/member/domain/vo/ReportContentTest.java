package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportContentTestUtils.testReportContent;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.REPORT_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReportContentTest {
    @Test
    @DisplayName("create으로 보고서 내용 반환")
    void testCreate_givenValidValue_willReturnReportContent() {
        assertThat(ReportContent.create(REPORT_CONTENT)).isEqualTo(ReportContent.create(REPORT_CONTENT));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ReportContent.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_CONTENT);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ReportContent.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_CONTENT);
    }

    @Test
    @DisplayName("600자를 초과하는 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenStringExceeding600Chars_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> ReportContent.create("a".repeat(601)));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.REPORT_CONTENT_OVER_LENGTH);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testReportContent, testReportContent);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testReportContent, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testReportContent, ReportContent.create(String.valueOf(UUID.randomUUID())));
    }
}