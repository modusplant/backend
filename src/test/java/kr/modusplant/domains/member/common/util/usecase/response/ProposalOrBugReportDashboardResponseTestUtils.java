package kr.modusplant.domains.member.common.util.usecase.response;

import kr.modusplant.domains.member.usecase.response.ProposalOrBugReportDashboardResponse;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_ULID;
import static kr.modusplant.domains.member.common.util.usecase.model.read.ProposalOrBugReportDashboardReadModelTestUtils.testProposalOrBugReportDashboardCheckedReadModelList;

public interface ProposalOrBugReportDashboardResponseTestUtils {
    ProposalOrBugReportDashboardResponse testProposalOrBugReportDashboardResponseWithFalseHasNext =
            ProposalOrBugReportDashboardResponse.of(
                    testProposalOrBugReportDashboardCheckedReadModelList, TEST_REPORT_ULID, false);

    ProposalOrBugReportDashboardResponse testProposalOrBugReportDashboardResponseWithTrueHasNext =
            ProposalOrBugReportDashboardResponse.of(
                    testProposalOrBugReportDashboardCheckedReadModelList, TEST_REPORT_ULID, true);
}
