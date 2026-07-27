package kr.modusplant.domains.member.usecase.port.mapper;

import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.ReportImagePath;
import kr.modusplant.domains.member.usecase.response.ProposalOrBugReportPrepareResponse;
import kr.modusplant.domains.member.usecase.response.ProposalOrBugReportPrepareResponse.ProposalOrBugReportImagePrepareResponse;

import java.util.List;

public interface ProposalOrBugReportMapper {
    ProposalOrBugReportImagePrepareResponse toProposalOrBugReportImagePrepareResponse(
            ReportImagePath imagePath, String storageUrl);

    ProposalOrBugReportPrepareResponse toProposalOrBugReportPrepareResponse(
            ReportId reportId, List<ProposalOrBugReportImagePrepareResponse> imagePrepareResponse);
}
