package kr.modusplant.domains.member.domain.aggregate;

import kr.modusplant.domains.member.common.util.domain.aggregate.ProposalOrBugReportTestUtils;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.shared.exception.EmptyValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.entity.ReportImageTestUtils.testProposalOrBugReportImages;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportContentTestUtils.testReportContent;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportTitleTestUtils.testReportTitle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProposalOrBugReportTest implements ProposalOrBugReportTestUtils {
    @DisplayName("null 값으로 create 호출")
    @Test
    void testCreate_givenNullToOneOfFourParameters_willThrowException() {
        // ReportId가 null일 때
        // given
        EmptyValueException reportIdException = assertThrows(EmptyValueException.class, () -> 
                ProposalOrBugReport.create(null, testReportTitle, testReportContent, testProposalOrBugReportImages));

        // when & then
        assertThat(reportIdException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_ID);

        // ReportTitle이 null일 때
        // given
        EmptyValueException reportTitleException = assertThrows(EmptyValueException.class, () -> 
                ProposalOrBugReport.create(testReportId, null, testReportContent, testProposalOrBugReportImages));

        // when & then
        assertThat(reportTitleException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_TITLE);

        // ReportContent가 null일 때
        // given
        EmptyValueException reportContentException = assertThrows(EmptyValueException.class, () ->
                ProposalOrBugReport.create(testReportId, testReportTitle, null, testProposalOrBugReportImages));

        // when & then
        assertThat(reportContentException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_CONTENT);

        // List<ReportImage>가 null일 때
        // given
        EmptyValueException reportImagesException = assertThrows(EmptyValueException.class, () ->
                ProposalOrBugReport.create(testReportId, testReportTitle, testReportContent, null));

        // when & then
        assertThat(reportImagesException.getErrorCode()).isEqualTo(MemberErrorCode.EMPTY_REPORT_IMAGE);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        ProposalOrBugReport proposalOrBugReport = createProposalOrBugReport();

        // when & then
        //noinspection EqualsWithItself
        assertEquals(proposalOrBugReport, proposalOrBugReport);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(createProposalOrBugReport(), testReportId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
        ProposalOrBugReport proposalOrBugReport = createProposalOrBugReport();
        assertNotEquals(proposalOrBugReport, ProposalOrBugReport.create(
                ReportId.generate(), testReportTitle, testReportContent, testProposalOrBugReportImages));
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        ProposalOrBugReport proposalOrBugReport = createProposalOrBugReport();
        assertEquals(proposalOrBugReport.hashCode(), proposalOrBugReport.hashCode());
    }
}