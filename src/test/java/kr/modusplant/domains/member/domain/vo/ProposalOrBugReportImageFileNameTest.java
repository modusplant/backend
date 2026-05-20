package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_IMAGE_FILE_NAME_1;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_IMAGE_FILE_NAME_2;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageFileNameTestUtils.testProposalOrBugReportImageFileName1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProposalOrBugReportImageFileNameTest {
    @Test
    @DisplayName("create으로 건의 및 버그 제보 파일명 반환")
    void testCreate_givenValidValue_willReturnProposalOrBugReportImageFileName() {
        assertThat(ProposalOrBugReportImageFileName.create(TEST_REPORT_IMAGE_FILE_NAME_1)).isEqualTo(ProposalOrBugReportImageFileName.create(TEST_REPORT_IMAGE_FILE_NAME_1));
    }

    @Test
    @DisplayName("null로 create을 호출하여 오류 발생")
    void testCreate_givenNull_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ProposalOrBugReportImageFileName.create(null));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_IMAGE_FILE_NAME);
    }

    @Test
    @DisplayName("빈 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenEmptyString_willThrowException() {
        EmptyValueException exception = assertThrows(EmptyValueException.class, () -> ProposalOrBugReportImageFileName.create("   "));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_IMAGE_FILE_NAME);
    }

    @Test
    @DisplayName("파일 확장자를 포함하지 않는 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenStringNotContainingExtension_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> ProposalOrBugReportImageFileName.create("InvalidData"));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.INVALID_REPORT_IMAGE_FILE_NAME);
    }

    @Test
    @DisplayName("정해진 파일명에 포함되지 않는 문자열로 create을 호출하여 오류 발생")
    void testCreate_givenStringWithInvalidFilename_willThrowException() {
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> ProposalOrBugReportImageFileName.create("InvalidData.png"));
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.INVALID_REPORT_IMAGE_FILE_NAME);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testProposalOrBugReportImageFileName1, testProposalOrBugReportImageFileName1);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testProposalOrBugReportImageFileName1, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testProposalOrBugReportImageFileName1, ProposalOrBugReportImageFileName.create(TEST_REPORT_IMAGE_FILE_NAME_2));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testProposalOrBugReportImageFileName1.hashCode(), testProposalOrBugReportImageFileName1.hashCode());
    }
}