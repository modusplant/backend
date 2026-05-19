package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImagePathTestUtils.testReportImagePath1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReportImagePathTest {
    @Test
    @DisplayName("create으로 보고서 이미지 경로 반환")
    void testCreate_givenValidValue_willReturnReportImagePath() {
        assertThat(ReportImagePath.create(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1)).isEqualTo(ReportImagePath.create(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ReportImagePath.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_IMAGE_PATH);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ReportImagePath.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_IMAGE_PATH);
    }

    @Test
    @DisplayName("정규 표현식에 매칭되지 않는 값으로 create을 호출하여 오류 발생")
    void testCreate_givenInvalidValue_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> ReportImagePath.create("invalid-data"));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.INVALID_REPORT_IMAGE_PATH);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testReportImagePath1, testReportImagePath1);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testReportImagePath1, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        UUID id = UUID.randomUUID();
        assertNotEquals(testReportImagePath1, ReportImagePath.create(String.format("member/%s/report/%s", id, "image.png")));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testReportImagePath1.hashCode(), testReportImagePath1.hashCode());
    }
}