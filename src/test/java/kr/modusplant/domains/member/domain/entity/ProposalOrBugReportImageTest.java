package kr.modusplant.domains.member.domain.entity;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.entity.ReportImageTestUtils.testProposalOrBugReportImage1;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageBytesTestUtils.testReportImageBytes1;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageBytesTestUtils.testReportImageBytes3;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageFileNameTestUtils.testProposalOrBugReportImageFileName1;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageFileNameTestUtils.testProposalOrBugReportImageFileName3;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImagePathTestUtils.testReportImagePath1;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImagePathTestUtils.testReportImagePath3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProposalOrBugReportImageTest {
    @DisplayName("null 값으로 create 호출")
    @Test
    void testCreate_givenNullToOneOfTwoParameters_willThrowException() {
        // ReportImagePath가 null일 때
        // given
        EmptyValueException emptyReportImagePathException =
                assertThrows(EmptyValueException.class,
                        () -> ProposalOrBugReportImage.create(null, testProposalOrBugReportImageFileName1, testReportImageBytes1));

        // when & then
        assertThat(emptyReportImagePathException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_IMAGE_PATH);

        // ReportImageFileName이 null일 때
        // given
        EmptyValueException emptyReportImageFileNameException =
                assertThrows(EmptyValueException.class,
                        () -> ProposalOrBugReportImage.create(testReportImagePath1, null, testReportImageBytes1));

        // when & then
        assertThat(emptyReportImageFileNameException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_IMAGE_FILE_NAME);

        // ReportImageBytes가 null일 때
        // given
        EmptyValueException emptyReportImageBytesException =
                assertThrows(EmptyValueException.class,
                        () -> ProposalOrBugReportImage.create(testReportImagePath1, testProposalOrBugReportImageFileName1, null));

        // when & then
        assertThat(emptyReportImageBytesException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_IMAGE_BYTES);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testProposalOrBugReportImage1, testProposalOrBugReportImage1);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testProposalOrBugReportImage1, testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testProposalOrBugReportImage1,
                ProposalOrBugReportImage.create(testReportImagePath3, testProposalOrBugReportImageFileName3, testReportImageBytes3));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testProposalOrBugReportImage1.hashCode(), testProposalOrBugReportImage1.hashCode());
    }
}