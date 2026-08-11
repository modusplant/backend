package kr.modusplant.domains.member.common.util.usecase.response;

import kr.modusplant.domains.member.usecase.response.ProposalOrBugReportPrepareResponse;

import java.util.List;

import static kr.modusplant.domains.member.common.constant.ReportConstant.*;

public interface ProposalOrBugReportPrepareResponseTestUtils {
    ProposalOrBugReportPrepareResponse.ProposalOrBugReportImagePrepareResponse testProposalOrBugReportImagePrepareResponse1 =
            new ProposalOrBugReportPrepareResponse.ProposalOrBugReportImagePrepareResponse(
                    TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1, TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_STORAGE_URL_1);
    ProposalOrBugReportPrepareResponse.ProposalOrBugReportImagePrepareResponse testProposalOrBugReportImagePrepareResponse2 =
            new ProposalOrBugReportPrepareResponse.ProposalOrBugReportImagePrepareResponse(
                    TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_2, TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_STORAGE_URL_2);
    ProposalOrBugReportPrepareResponse.ProposalOrBugReportImagePrepareResponse testProposalOrBugReportImagePrepareResponse3 =
            new ProposalOrBugReportPrepareResponse.ProposalOrBugReportImagePrepareResponse(
                    TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_3, TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_STORAGE_URL_3);
    ProposalOrBugReportPrepareResponse testProposalOrBugReportPrepareResponse =
            ProposalOrBugReportPrepareResponse.of(
                    TEST_REPORT_ULID,
                    List.of(testProposalOrBugReportImagePrepareResponse1,
                            testProposalOrBugReportImagePrepareResponse2,
                            testProposalOrBugReportImagePrepareResponse3));
}
