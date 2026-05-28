package kr.modusplant.domains.member.framework.outbound;

import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.ReportPageSize;
import kr.modusplant.domains.member.domain.vo.ReportTime;
import kr.modusplant.domains.member.framework.outbound.jooq.record.ProposalOrBugReportDashboardRecord;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ProposalOrBugReportDashboardJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportDashboardEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.ProposalOrBugReportEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.PostAbuseReportDashboardJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.ProposalOrBugReportJpaRepository;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.port.repository.ReportDashboardRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReportDashboardRepositoryAdapter implements ReportDashboardRepository {
    private final PostJpaRepository postJpaRepository;
    private final ProposalOrBugReportJpaRepository proposalOrBugReportJpaRepository;
    private final PostAbuseReportDashboardJpaRepository postAbuseReportDashboardJpaRepository;

    private final ProposalOrBugReportDashboardJooqRepository proposalOrBugReportDashboardJooqRepository;

    @Override
    public ProposalOrBugReportDashboardReadModel checkProposalOrBugReport(ReportId reportId) {
        ProposalOrBugReportEntity proposalOrBugReportEntity = proposalOrBugReportJpaRepository.findByUlid(reportId.getValue()).orElseThrow();
        proposalOrBugReportEntity.check();
        proposalOrBugReportJpaRepository.save(proposalOrBugReportEntity);
        return proposalOrBugReportDashboardJooqRepository.getDashboardReadModel(
                proposalOrBugReportDashboardJooqRepository.getDashboardByReportId(reportId.getValue()));
    }

    @Override
    public List<ProposalOrBugReportDashboardReadModel> getProposalOrBugReports(
            ReportPageSize reportPageSize,
            @Nullable ProposalOrBugReportStatus proposalOrBugReportStatus,
            @Nullable ReportId lastReportId) {

        String statusResult = proposalOrBugReportStatus != null ? proposalOrBugReportStatus.name() : null;
        String reportIdResult = lastReportId != null ? lastReportId.getValue() : null;
        List<ProposalOrBugReportDashboardRecord> records =
                proposalOrBugReportDashboardJooqRepository.getDashboardsByPageSizeAndStatus(
                        reportPageSize.getValue(), statusResult, reportIdResult);
        return proposalOrBugReportDashboardJooqRepository.getProposalOrBugReportDashboardReadModels(records);
    }

    @Override
    public void reflectPostAbuseReport(ActivitySubjectPostId postIdVO, ReportTime reportTimeVO) {
        String postId = postIdVO.getValue();
        LocalDateTime reportTime = reportTimeVO.getValue();
        Optional<PostAbuseReportDashboardEntity> optionalDashboardEntity =
                postAbuseReportDashboardJpaRepository.findById(postId);
        if (optionalDashboardEntity.isEmpty()) {
            postAbuseReportDashboardJpaRepository.save(
                    PostAbuseReportDashboardEntity.builder()
                            .post(postJpaRepository.findByUlid(postId).orElseThrow())
                            .firstReportedAt(reportTime)
                            .lastReportedAt(reportTime)
                            .build()
            );
        } else {
            PostAbuseReportDashboardEntity dashboardEntity =
                    postAbuseReportDashboardJpaRepository.findById(postId).orElseThrow();
            dashboardEntity.increaseReportCount();
            dashboardEntity.updateLastReportedAt(reportTime);
            postAbuseReportDashboardJpaRepository.save(dashboardEntity);
        }
    }
}
