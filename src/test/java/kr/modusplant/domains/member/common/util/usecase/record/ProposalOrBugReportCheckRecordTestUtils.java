package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportCheckRecord;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_ULID;

public interface ProposalOrBugReportCheckRecordTestUtils {
    ProposalOrBugReportCheckRecord testProposalOrBugReportCheckRecord =
            new ProposalOrBugReportCheckRecord(TEST_REPORT_ULID);
}
