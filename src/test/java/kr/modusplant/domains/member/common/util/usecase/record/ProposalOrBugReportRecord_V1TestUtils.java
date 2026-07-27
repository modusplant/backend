package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRecord_V1;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.ReportConstant.*;

public interface ProposalOrBugReportRecord_V1TestUtils {
    ProposalOrBugReportRecord_V1 testProposalOrBugReportRecord_v1 =
            new ProposalOrBugReportRecord_V1(
                    MEMBER_BASIC_USER_UUID,
                    TEST_REPORT_TITLE,
                    TEST_REPORT_CONTENT,
                    TEST_REPORT_IMAGES,
                    TEST_REPORT_IMAGE_NUMBER_3);
}
