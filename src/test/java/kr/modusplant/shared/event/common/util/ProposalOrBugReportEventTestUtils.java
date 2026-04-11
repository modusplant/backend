package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.ProposalOrBugReportEvent;

import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface ProposalOrBugReportEventTestUtils {
    ProposalOrBugReportEvent testProposalOrBugReportEvent = ProposalOrBugReportEvent.create(MEMBER_BASIC_USER_UUID, TEST_REPORT_TITLE, TEST_REPORT_CONTENT, TEST_REPORT_IMAGE_PATH);
}
