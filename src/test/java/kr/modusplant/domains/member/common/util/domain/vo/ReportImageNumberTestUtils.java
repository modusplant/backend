package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ProposalOrBugReportImageNumber;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_IMAGE_NUMBER_3;

public interface ReportImageNumberTestUtils {
    ProposalOrBugReportImageNumber testProposalOrBugReportImageNumber3 = ProposalOrBugReportImageNumber.create(TEST_REPORT_IMAGE_NUMBER_3);
    ProposalOrBugReportImageNumber testProposalOrBugReportImageNumberEmpty = ProposalOrBugReportImageNumber.createEmpty();
}
