package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_IMAGE_NUMBER_3;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageNumberTestUtils.testProposalOrBugReportImageNumber3;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageNumberTestUtils.testProposalOrBugReportImageNumberEmpty;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.PROPOSAL_OR_BUG_REPORT_IMAGE_NUMBER_OUT_OF_RANGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProposalOrBugReportImageNumberTest {
    @Test
    @DisplayName("0보다 작은 값으로 create 호출 시 예외 발생")
    void testCreate_givenValueLessThanZero_willThrowException() {
        // given & when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> ProposalOrBugReportImageNumber.create(-1));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PROPOSAL_OR_BUG_REPORT_IMAGE_NUMBER_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("3보다 큰 값으로 create 호출 시 예외 발생")
    void testCreate_givenValueGreaterThanThree_willThrowException() {
        // given & when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> ProposalOrBugReportImageNumber.create(4));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PROPOSAL_OR_BUG_REPORT_IMAGE_NUMBER_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("경계값(0과 3)으로 create 호출 시 정상 객체 반환")
    void testCreate_givenBoundaryValues_willReturnProposalOrBugReportImageNumber() {
        // given & when
        ProposalOrBugReportImageNumber minImageNumber = ProposalOrBugReportImageNumber.create(0);
        ProposalOrBugReportImageNumber maxImageNumber = ProposalOrBugReportImageNumber.create(3);

        // then
        assertEquals(0, minImageNumber.getValueIfNotEmpty());
        assertEquals(3, maxImageNumber.getValueIfNotEmpty());
    }

    @Test
    @DisplayName("null로 create을 호출하여 비어 있는 보고서 이미지 개수 반환")
    void testCreate_givenNull_willThrowException() {
        assertThat(ProposalOrBugReportImageNumber.create(null)).isEqualTo(testProposalOrBugReportImageNumberEmpty);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testProposalOrBugReportImageNumber3, testProposalOrBugReportImageNumber3);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testProposalOrBugReportImageNumber3, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testProposalOrBugReportImageNumber3, ProposalOrBugReportImageNumber.create(TEST_REPORT_IMAGE_NUMBER_3 - 1));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testProposalOrBugReportImageNumber3.hashCode(), testProposalOrBugReportImageNumber3.hashCode());
    }
}