package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ProposalOrBugReportImageFileName;

import static kr.modusplant.domains.member.common.constant.ReportConstant.*;

public interface ReportImageFileNameTestUtils {
    ProposalOrBugReportImageFileName testProposalOrBugReportImageFileName1 = ProposalOrBugReportImageFileName.create(TEST_REPORT_IMAGE_FILE_NAME_1);
    ProposalOrBugReportImageFileName testProposalOrBugReportImageFileName2 = ProposalOrBugReportImageFileName.create(TEST_REPORT_IMAGE_FILE_NAME_2);
    ProposalOrBugReportImageFileName testProposalOrBugReportImageFileName3 = ProposalOrBugReportImageFileName.create(TEST_REPORT_IMAGE_FILE_NAME_3);
}
