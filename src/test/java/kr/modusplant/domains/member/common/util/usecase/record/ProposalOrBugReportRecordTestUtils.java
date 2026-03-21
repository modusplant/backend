package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRecord;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN;

public interface ProposalOrBugReportRecordTestUtils {
    ProposalOrBugReportRecord testProposalOrBugReportRecord = new ProposalOrBugReportRecord(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN, TEST_REPORT_TITLE, TEST_REPORT_CONTENT, TEST_REPORT_IMAGE);
}
