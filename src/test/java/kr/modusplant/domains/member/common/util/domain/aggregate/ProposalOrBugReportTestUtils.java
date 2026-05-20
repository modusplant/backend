package kr.modusplant.domains.member.common.util.domain.aggregate;

import kr.modusplant.domains.member.domain.aggregate.ProposalOrBugReport;

import static kr.modusplant.domains.member.common.util.domain.entity.ReportImageTestUtils.testProposalOrBugReportImages;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportContentTestUtils.testReportContent;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportTitleTestUtils.testReportTitle;

public interface ProposalOrBugReportTestUtils {
    default ProposalOrBugReport createProposalOrBugReport() {
        return ProposalOrBugReport.create(testReportId, testReportTitle, testReportContent, testProposalOrBugReportImages);
    }
}
