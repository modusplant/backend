package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.ProposalOrBugReportEvent;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.ReportConstant.*;

public interface ProposalOrBugReportEventTestUtils {
    ProposalOrBugReportEvent testProposalOrBugReportEvent =
            ProposalOrBugReportEvent.create(
                    MEMBER_BASIC_USER_UUID,
                    TEST_REPORT_ULID,
                    TEST_REPORT_TITLE,
                    TEST_REPORT_CONTENT,
                    TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_FILE_NAMES,
                    TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATHS);
}
