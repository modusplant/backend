package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRemoveRecord;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface ProposalOrBugReportRemoveRecordTestUtils {
    ProposalOrBugReportRemoveRecord testProposalOrBugReportRemoveRecord =
            new ProposalOrBugReportRemoveRecord(MEMBER_BASIC_USER_UUID, TEST_REPORT_ULID);
}
