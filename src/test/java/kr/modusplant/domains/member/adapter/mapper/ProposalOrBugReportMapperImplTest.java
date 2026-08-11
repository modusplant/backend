package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.usecase.port.mapper.ProposalOrBugReportMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_STORAGE_URL_1;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportImagePathTestUtils.testReportImagePath1;
import static kr.modusplant.domains.member.common.util.usecase.response.ProposalOrBugReportPrepareResponseTestUtils.testProposalOrBugReportImagePrepareResponse1;
import static kr.modusplant.domains.member.common.util.usecase.response.ProposalOrBugReportPrepareResponseTestUtils.testProposalOrBugReportImagePrepareResponse2;
import static kr.modusplant.domains.member.common.util.usecase.response.ProposalOrBugReportPrepareResponseTestUtils.testProposalOrBugReportImagePrepareResponse3;
import static kr.modusplant.domains.member.common.util.usecase.response.ProposalOrBugReportPrepareResponseTestUtils.testProposalOrBugReportPrepareResponse;
import static org.assertj.core.api.Assertions.assertThat;

class ProposalOrBugReportMapperImplTest {
    private final ProposalOrBugReportMapper proposalOrBugReportMapper = new ProposalOrBugReportMapperImpl();

    @Test
    @DisplayName("toProposalOrBugReportImagePrepareResponse로 응답 반환")
    void testToProposalOrBugReportImagePrepareResponse_givenValidData_willReturnResponse() {
        assertThat(proposalOrBugReportMapper.toProposalOrBugReportImagePrepareResponse(testReportImagePath1, TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_STORAGE_URL_1))
                .isEqualTo(testProposalOrBugReportImagePrepareResponse1);
    }

    @Test
    @DisplayName("toProposalOrBugReportPrepareResponse로 응답 반환")
    void testToProposalOrBugReportPrepareResponse_givenValidData_willReturnResponse() {
        assertThat(proposalOrBugReportMapper.toProposalOrBugReportPrepareResponse(
                testReportId,
                List.of(testProposalOrBugReportImagePrepareResponse1, testProposalOrBugReportImagePrepareResponse2, testProposalOrBugReportImagePrepareResponse3)))
                .isEqualTo(testProposalOrBugReportPrepareResponse);
    }
}
