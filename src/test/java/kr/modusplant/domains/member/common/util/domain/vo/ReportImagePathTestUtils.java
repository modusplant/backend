package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ReportImagePath;

import static kr.modusplant.domains.member.common.constant.ReportConstant.*;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImageFileNameTestUtils.testProposalOrBugReportImageFileName1;

public interface ReportImagePathTestUtils {
    ReportImagePath testReportImagePath1 = ReportImagePath.create(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1);
    ReportImagePath testReportImagePath2 = ReportImagePath.create(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_2);
    ReportImagePath testReportImagePath3 = ReportImagePath.create(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_3);
    ReportImagePath testReportImagePathFromMemberIdAndReportId =
            ReportImagePath.create(testMemberId, testReportId, testProposalOrBugReportImageFileName1);
}
