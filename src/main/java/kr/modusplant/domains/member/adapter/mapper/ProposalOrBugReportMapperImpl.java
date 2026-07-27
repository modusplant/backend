package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.ReportImagePath;
import kr.modusplant.domains.member.usecase.port.mapper.ProposalOrBugReportMapper;
import kr.modusplant.domains.member.usecase.response.ProposalOrBugReportPrepareResponse;
import kr.modusplant.domains.member.usecase.response.ProposalOrBugReportPrepareResponse.ProposalOrBugReportImagePrepareResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProposalOrBugReportMapperImpl implements ProposalOrBugReportMapper {
    @Override
    public ProposalOrBugReportImagePrepareResponse toProposalOrBugReportImagePrepareResponse(
            ReportImagePath imagePath, String storageUrl) {
        return new ProposalOrBugReportImagePrepareResponse(imagePath.getValue(), storageUrl);
    }

    @Override
    public ProposalOrBugReportPrepareResponse toProposalOrBugReportPrepareResponse(
            ReportId reportId, List<ProposalOrBugReportImagePrepareResponse> imagePrepareResponse) {
        return ProposalOrBugReportPrepareResponse.of(reportId.getValue(), imagePrepareResponse);
    }
}
