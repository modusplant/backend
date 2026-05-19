package kr.modusplant.domains.member.domain.aggregate;

import kr.modusplant.domains.member.common.util.domain.aggregate.ReportTestUtils;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static kr.modusplant.domains.member.common.util.domain.entity.ReportImageTestUtils.testReportImages;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportContentTestUtils.testReportContent;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageNumberTestUtils.testProposalOrBugReportImageNumber3;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportTitleTestUtils.testReportTitle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReportTest implements ReportTestUtils {
    @DisplayName("null 값으로 create 호출")
    @Test
    void testCreate_givenNullToOneOfFourParameters_willThrowException() {
        // ReportId가 null일 때
        // given
        EmptyValueException reportIdException = assertThrows(EmptyValueException.class, () -> 
                ProposalOrBugReport.create(null, testReportTitle, testReportContent, testReportImages, testProposalOrBugReportImageNumber3));

        // when & then
        assertThat(reportIdException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_ID);

        // ReportTitle이 null일 때
        // given
        EmptyValueException reportTitleException = assertThrows(EmptyValueException.class, () -> 
                ProposalOrBugReport.create(testReportId, null, testReportContent, testReportImages, testProposalOrBugReportImageNumber3));

        // when & then
        assertThat(reportTitleException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_TITLE);

        // ReportContent가 null일 때
        // given
        EmptyValueException reportContentException = assertThrows(EmptyValueException.class, () ->
                ProposalOrBugReport.create(testReportId, testReportTitle, null, testReportImages, testProposalOrBugReportImageNumber3));

        // when & then
        assertThat(reportContentException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_CONTENT);

        // List<ReportImage>가 null일 때
        // given
        EmptyValueException reportImagesException = assertThrows(EmptyValueException.class, () ->
                ProposalOrBugReport.create(testReportId, testReportTitle, testReportContent, null, testProposalOrBugReportImageNumber3));

        // when & then
        assertThat(reportImagesException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_IMAGE);

        // ReportImageNumber가 null일 때
        // given
        EmptyValueException reportImageNumberException = assertThrows(EmptyValueException.class, () ->
                ProposalOrBugReport.create(testReportId, testReportTitle, testReportContent, testReportImages, null));

        // when & then
        assertThat(reportImageNumberException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_IMAGE_NUMBER);

        // List<ReportImage>와 ReportImageNumber가 매칭되지 않을 때
        // given
        InvalidValueException mismatchException = assertThrows(InvalidValueException.class, () ->
                ProposalOrBugReport.create(testReportId, testReportTitle, testReportContent, List.of(), testProposalOrBugReportImageNumber3));

        // when & then
        assertThat(mismatchException.getErrorCode()).isEqualTo(MemberErrorCode.MISMATCHED_REPORT_IMAGE_SIZE);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        ProposalOrBugReport proposalOrBugReport = createReport();

        // when & then
        //noinspection EqualsWithItself
        assertEquals(proposalOrBugReport, proposalOrBugReport);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(createReport(), testReportId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        ProposalOrBugReport proposalOrBugReport = createReport();
        assertNotEquals(proposalOrBugReport, ProposalOrBugReport.create(
                ReportId.generate(), testReportTitle, testReportContent, testReportImages, testProposalOrBugReportImageNumber3));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        ProposalOrBugReport proposalOrBugReport = createReport();
        assertEquals(proposalOrBugReport.hashCode(), proposalOrBugReport.hashCode());
    }
}