package kr.modusplant.domains.member.framework.outbound;

import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.CommentAbuseReportDashboardJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.PostAbuseReportDashboardJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ProposalOrBugReportDashboardJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.compositekey.CommentAbuseReportDashboardCompositeKey;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.CommentAbuseReportDashboardEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportDashboardEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.ProposalOrBugReportEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.CommentAbuseReportDashboardJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.PostAbuseReportDashboardJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.ProposalOrBugReportJpaRepository;
import kr.modusplant.domains.member.usecase.model.read.CommentAbuseReportDashboardReadModel;
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
    private final CommentAbuseReportDashboardJpaRepository commentAbuseReportDashboardJpaRepository;

    private final ProposalOrBugReportDashboardJooqRepository proposalOrBugReportDashboardJooqRepository;
    private final PostAbuseReportDashboardJooqRepository postAbuseReportDashboardJooqRepository;
    private final CommentAbuseReportDashboardJooqRepository commentAbuseReportDashboardJooqRepository;

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
        proposalOrBugReportJpaRepository.saveAndFlush(proposalOrBugReportEntity);
        return proposalOrBugReportDashboardJooqRepository.getReadModelByReportId(reportId.getValue());
    }

    @Override
    public List<PostAbuseReportDashboardReadModel> getPostAbuseReports(
            ReportPageSize reportPageSize,
            @Nullable AbuseReportStatus status,
            @Nullable ActivitySubjectPostId lastPostId) {

        String statusResult = status != null ? status.name() : null;
        String lastPostIdResult = lastPostId != null ? lastPostId.getValue() : null;
        return postAbuseReportDashboardJooqRepository.getReadModelsByPageSizeAndStatusAndPostUlid(
                        reportPageSize.getValue(), statusResult, lastPostIdResult);
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
        postAbuseReportDashboardJpaRepository.saveAndFlush(entity);
        return postAbuseReportDashboardJooqRepository.getReadModelByPostId(postId.getValue());
    }

    @Override
    public PostAbuseReportDashboardReadModel approvePostAbuseReport(ActivitySubjectPostId postId) {
        PostAbuseReportDashboardEntity entity =
                postAbuseReportDashboardJpaRepository.findById(postId.getValue())
                        .orElseThrow(() ->
                                new NotFoundEntityException(NOT_FOUND_POST_ABUSE_REPORT, "postAbuseReport"));
        entity.approve();
        postAbuseReportDashboardJpaRepository.saveAndFlush(entity);
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

    @Override
    public List<CommentAbuseReportDashboardReadModel> getCommentAbuseReports(
            ReportPageSize reportPageSize,
            @Nullable AbuseReportStatus status,
            @Nullable ActivitySubjectCommentId lastCommentId) {
        String statusResult = status != null ? status.name() : null;
        String lastPostIdResult = lastCommentId != null ? lastCommentId.getActivitySubjectPostId().getValue() : null;
        String lastCommentPathResult = lastCommentId != null ? lastCommentId.getActivitySubjectCommentPath().getValue() : null;
        return commentAbuseReportDashboardJooqRepository.getReadModelsByPageSizeAndStatusAndCursor(
                reportPageSize.getValue(), statusResult, lastPostIdResult, lastCommentPathResult);
    }

    @Override
    public void reflectCommentAbuseReport(ActivitySubjectCommentId commentId, ReportTime reportTimeVO) {
        String postUlid = commentId.getActivitySubjectPostId().getValue();
        String path = commentId.getActivitySubjectCommentPath().getValue();
        LocalDateTime reportTime = reportTimeVO.getValue();
        CommentAbuseReportDashboardCompositeKey compositeKey =
                new CommentAbuseReportDashboardCompositeKey(postUlid, path);
        Optional<CommentAbuseReportDashboardEntity> optionalDashboardEntity =
                commentAbuseReportDashboardJpaRepository.findById(compositeKey);
        if (optionalDashboardEntity.isEmpty()) {
            commentAbuseReportDashboardJpaRepository.save(
                    CommentAbuseReportDashboardEntity.builder()
                            .postUlid(postUlid)
                            .path(path)
                            .firstReportedAt(reportTime)
                            .lastReportedAt(reportTime)
                            .build()
            );
        } else {
            CommentAbuseReportDashboardEntity dashboardEntity =
                    commentAbuseReportDashboardJpaRepository.findById(compositeKey)
                            .orElseThrow(() ->
                                    new NotFoundEntityException(
                                            NOT_FOUND_COMMENT_ABUSE_REPORT_DASHBOARD, "commentAbuseReportDashboard"));
            dashboardEntity.increaseReportCount();
            dashboardEntity.updateLastReportedAt(reportTime);
            commentAbuseReportDashboardJpaRepository.save(dashboardEntity);
        }
    }

    @Override
    public CommentAbuseReportDashboardReadModel dismissCommentAbuseReport(ActivitySubjectCommentId commentId) {
        String postUlid = commentId.getActivitySubjectPostId().getValue();
        String path = commentId.getActivitySubjectCommentPath().getValue();
        CommentAbuseReportDashboardCompositeKey compositeKey =
                new CommentAbuseReportDashboardCompositeKey(postUlid, path);
        CommentAbuseReportDashboardEntity dashboardEntity =
                commentAbuseReportDashboardJpaRepository.findById(compositeKey)
                        .orElseThrow(() ->
                                new NotFoundEntityException(NOT_FOUND_COMMENT_ABUSE_REPORT, "commentAbuseReport"));
        dashboardEntity.dismiss();
        commentAbuseReportDashboardJpaRepository.saveAndFlush(dashboardEntity);
        return commentAbuseReportDashboardJooqRepository.getReadModelByPostUlidAndPath(postUlid, path);
    }

    @Override
    public CommentAbuseReportDashboardReadModel approveCommentAbuseReport(ActivitySubjectCommentId commentId) {
        String postUlid = commentId.getActivitySubjectPostId().getValue();
        String path = commentId.getActivitySubjectCommentPath().getValue();
        CommentAbuseReportDashboardCompositeKey compositeKey =
                new CommentAbuseReportDashboardCompositeKey(postUlid, path);
        CommentAbuseReportDashboardEntity dashboardEntity =
                commentAbuseReportDashboardJpaRepository.findById(compositeKey)
                        .orElseThrow(() ->
                                new NotFoundEntityException(NOT_FOUND_COMMENT_ABUSE_REPORT, "commentAbuseReport"));
        dashboardEntity.approve();
        commentAbuseReportDashboardJpaRepository.saveAndFlush(dashboardEntity);
        return commentAbuseReportDashboardJooqRepository.getReadModelByPostUlidAndPath(postUlid, path);
    }

    @Override
    public boolean isDismissedInCommentAbuseReportDashboard(ActivitySubjectCommentId commentId) {
        String postUlid = commentId.getActivitySubjectPostId().getValue();
        String path = commentId.getActivitySubjectCommentPath().getValue();
        return commentAbuseReportDashboardJpaRepository
                .findById(new CommentAbuseReportDashboardCompositeKey(postUlid, path))
                .map(e -> e.getStatus() == AbuseReportStatus.DISMISSED)
                .orElse(false);
    }

    @Override
    public boolean isApprovedInCommentAbuseReportDashboard(ActivitySubjectCommentId commentId) {
        String postUlid = commentId.getActivitySubjectPostId().getValue();
        String path = commentId.getActivitySubjectCommentPath().getValue();
        return commentAbuseReportDashboardJpaRepository
                .findById(new CommentAbuseReportDashboardCompositeKey(postUlid, path))
                .map(e -> e.getStatus() == AbuseReportStatus.BLINDED)
                .orElse(false);
    }
}
