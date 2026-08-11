package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRecord_V2;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.ReportConstant.*;

public interface ProposalOrBugReportRecord_V2TestUtils {
    ProposalOrBugReportRecord_V2 testProposalOrBugReportRecord_v2 =
            new ProposalOrBugReportRecord_V2(
                    MEMBER_BASIC_USER_UUID,
                    TEST_REPORT_TITLE,
                    TEST_REPORT_CONTENT,
                    TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATHS);
}
