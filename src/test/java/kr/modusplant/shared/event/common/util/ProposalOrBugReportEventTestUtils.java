package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.ProposalOrBugReportEvent;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface ProposalOrBugReportEventTestUtils {
    ProposalOrBugReportEvent testProposalOrBugReportEvent = ProposalOrBugReportEvent.create(MEMBER_BASIC_USER_UUID, REPORT_TITLE, REPORT_CONTENT, REPORT_IMAGE_PATH);
}
