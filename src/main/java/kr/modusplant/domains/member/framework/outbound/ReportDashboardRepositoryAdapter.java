package kr.modusplant.domains.member.framework.outbound;

import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.ReportPageSize;
import kr.modusplant.domains.member.domain.vo.ReportTime;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.PostAbuseReportDashboardJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ProposalOrBugReportDashboardJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportDashboardEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.ProposalOrBugReportEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.PostAbuseReportDashboardJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.ProposalOrBugReportJpaRepository;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.port.repository.ReportDashboardRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;
import static kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode.NOT_FOUND_POST;

@Repository
@RequiredArgsConstructor
public class ReportDashboardRepositoryAdapter implements ReportDashboardRepository {
    private final PostJpaRepository postJpaRepository;
    private final ProposalOrBugReportJpaRepository proposalOrBugReportJpaRepository;
    private final PostAbuseReportDashboardJpaRepository postAbuseReportDashboardJpaRepository;

    private final ProposalOrBugReportDashboardJooqRepository proposalOrBugReportDashboardJooqRepository;
    private final PostAbuseReportDashboardJooqRepository postAbuseReportDashboardJooqRepository;

    @Override
    public List<ProposalOrBugReportDashboardReadModel> getProposalOrBugReports(
            ReportPageSize reportPageSize,
            @Nullable ProposalOrBugReportStatus proposalOrBugReportStatus,
            @Nullable ReportId lastReportId) {

        String statusResult = proposalOrBugReportStatus != null ? proposalOrBugReportStatus.name() : null;
        String reportIdResult = lastReportId != null ? lastReportId.getValue() : null;
        return proposalOrBugReportDashboardJooqRepository.getReadModelsByPageSizeAndStatusAndReportId(
                        reportPageSize.getValue(), statusResult, reportIdResult);
    }

    @Override
    public ProposalOrBugReportDashboardReadModel checkProposalOrBugReport(ReportId reportId) {
        ProposalOrBugReportEntity proposalOrBugReportEntity =
                proposalOrBugReportJpaRepository.findByUlid(reportId.getValue())
                        .orElseThrow(() ->
                                new NotFoundEntityException(NOT_FOUND_PROPOSAL_OR_BUG_REPORT, "proposalOrBugReport"));
        proposalOrBugReportEntity.check();
        proposalOrBugReportJpaRepository.save(proposalOrBugReportEntity);
        return proposalOrBugReportDashboardJooqRepository.getReadModelByReportId(reportId.getValue());
    }

    @Override
    public List<PostAbuseReportDashboardReadModel> getPostAbuseReports(
            ReportPageSize reportPageSize,
            @Nullable AbuseReportStatus status,
            @Nullable String lastPostUlid) {

        String statusResult = status != null ? status.name() : null;
        return postAbuseReportDashboardJooqRepository.getReadModelsByPageSizeAndStatusAndPostUlid(
                        reportPageSize.getValue(), statusResult, lastPostUlid);
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
                            .post(postJpaRepository.findByUlid(postId)
                                    .orElseThrow(() ->
                                            new NotFoundEntityException(NOT_FOUND_POST, "post")))
                            .firstReportedAt(reportTime)
                            .lastReportedAt(reportTime)
                            .build()
            );
        } else {
            PostAbuseReportDashboardEntity dashboardEntity =
                    postAbuseReportDashboardJpaRepository.findById(postId)
                            .orElseThrow(() ->
                                    new NotFoundEntityException(
                                            NOT_FOUND_POST_ABUSE_REPORT_DASHBOARD, "postAbuseReportDashboard"));
            dashboardEntity.increaseReportCount();
            dashboardEntity.updateLastReportedAt(reportTime);
            postAbuseReportDashboardJpaRepository.save(dashboardEntity);
        }
    }

    @Override
    public PostAbuseReportDashboardReadModel dismissPostAbuseReport(ActivitySubjectPostId postId) {
        PostAbuseReportDashboardEntity entity =
                postAbuseReportDashboardJpaRepository.findById(postId.getValue())
                        .orElseThrow(() ->
                                new NotFoundEntityException(NOT_FOUND_POST_ABUSE_REPORT, "postAbuseReport"));
        entity.dismiss();
        postAbuseReportDashboardJpaRepository.save(entity);
        return postAbuseReportDashboardJooqRepository.getReadModelByPostId(postId.getValue());
    }

    @Override
    public PostAbuseReportDashboardReadModel approvePostAbuseReport(ActivitySubjectPostId postId) {
        PostAbuseReportDashboardEntity entity =
                postAbuseReportDashboardJpaRepository.findById(postId.getValue())
                        .orElseThrow(() ->
                                new NotFoundEntityException(NOT_FOUND_POST_ABUSE_REPORT, "postAbuseReport"));
        entity.approve();
        postAbuseReportDashboardJpaRepository.save(entity);
        return postAbuseReportDashboardJooqRepository.getReadModelByPostId(postId.getValue());
    }

    @Override
    public boolean isDismissedInPostAbuseReportDashboard(ActivitySubjectPostId postId) {
        return postAbuseReportDashboardJpaRepository.findById(postId.getValue())
                .map(e -> e.getStatus() == AbuseReportStatus.DISMISSED)
                .orElse(false);
    }

    @Override
    public boolean isApprovedInPostAbuseReportDashboard(ActivitySubjectPostId postId) {
        return postAbuseReportDashboardJpaRepository.findById(postId.getValue())
                .map(e -> e.getStatus() == AbuseReportStatus.BLINDED)
                .orElse(false);
    }
}
