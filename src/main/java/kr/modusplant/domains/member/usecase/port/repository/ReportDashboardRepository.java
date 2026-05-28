package kr.modusplant.domains.member.usecase.port.repository;

import jakarta.annotation.Nullable;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.ReportPageSize;
import kr.modusplant.domains.member.domain.vo.ReportTime;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;

import java.util.List;

public interface ReportDashboardRepository {
    ProposalOrBugReportDashboardReadModel checkProposalOrBugReport(ReportId reportId);

    List<ProposalOrBugReportDashboardReadModel> getProposalOrBugReports(
            ReportPageSize reportPageSize,
            @Nullable ProposalOrBugReportStatus proposalOrBugReportStatus,
            @Nullable ReportId lastReportId);

    void reflectPostAbuseReport(ActivitySubjectPostId postId, ReportTime reportTime);
}
