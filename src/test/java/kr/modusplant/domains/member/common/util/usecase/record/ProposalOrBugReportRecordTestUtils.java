package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRecord;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface ProposalOrBugReportRecordTestUtils {
    ProposalOrBugReportRecord testProposalOrBugReportRecord = new ProposalOrBugReportRecord(MEMBER_BASIC_USER_UUID, REPORT_TITLE, REPORT_CONTENT, REPORT_IMAGE);
}
