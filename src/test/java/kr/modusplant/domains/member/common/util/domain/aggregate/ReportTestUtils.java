package kr.modusplant.domains.member.common.util.domain.aggregate;

import kr.modusplant.domains.member.domain.aggregate.ProposalOrBugReport;

import static kr.modusplant.domains.member.common.util.domain.entity.ReportImageTestUtils.testReportImages;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportContentTestUtils.testReportContent;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageNumberTestUtils.testProposalOrBugReportImageNumber3;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportTitleTestUtils.testReportTitle;

public interface ReportTestUtils {
    default ProposalOrBugReport createReport() {
        return ProposalOrBugReport.create(testReportId, testReportTitle, testReportContent, testReportImages, testProposalOrBugReportImageNumber3);
    }
}
