package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportGetRecord;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_SIZE;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_ULID;

public interface ProposalOrBugReportRecordGetTestUtils {
    ProposalOrBugReportGetRecord testProposalOrBugReportGetRecord =
            new ProposalOrBugReportGetRecord(
                    TEST_REPORT_ULID, TEST_REPORT_SIZE);
}
