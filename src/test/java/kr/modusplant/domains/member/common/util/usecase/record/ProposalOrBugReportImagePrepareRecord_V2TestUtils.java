package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportImagePrepareRecord_V2;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_IMAGE_CONTENT_TYPES;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_IMAGE_FILE_NAMES;

public interface ProposalOrBugReportImagePrepareRecord_V2TestUtils {
    ProposalOrBugReportImagePrepareRecord_V2 testProposalOrBugReportImagePrepareRecord_V2 =
            new ProposalOrBugReportImagePrepareRecord_V2(MEMBER_BASIC_USER_UUID, TEST_REPORT_IMAGE_FILE_NAMES, TEST_REPORT_IMAGE_CONTENT_TYPES);
}
