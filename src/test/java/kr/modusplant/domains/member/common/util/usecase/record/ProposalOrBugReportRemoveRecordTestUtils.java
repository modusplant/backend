package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRemoveRecord;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_ULID;

public interface ProposalOrBugReportRemoveRecordTestUtils {
    ProposalOrBugReportRemoveRecord testProposalOrBugReportRemoveRecord =
            new ProposalOrBugReportRemoveRecord(TEST_REPORT_ULID);
}
