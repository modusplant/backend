package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportTitleTestUtils.testReportTitle;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.REPORT_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReportTitleTest {
    @Test
    @DisplayName("create으로 보고서 제목 반환")
    void testCreate_givenValidValue_willReturnReportTitle() {
        assertThat(ReportTitle.create(REPORT_TITLE)).isEqualTo(ReportTitle.create(REPORT_TITLE));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ReportTitle.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_TITLE);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ReportTitle.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_TITLE);
    }

    @Test
    @DisplayName("60자를 초과하는 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenStringExceeding60Chars_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> ReportTitle.create("a".repeat(61)));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.REPORT_TITLE_OVER_LENGTH);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testReportTitle, testReportTitle);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testReportTitle, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testReportTitle, ReportTitle.create(String.valueOf(UUID.randomUUID())));
    }
}