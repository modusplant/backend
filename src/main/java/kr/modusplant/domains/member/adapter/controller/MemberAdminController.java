package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.domain.event.CommentAbuseReportApproveEvent;
import kr.modusplant.domains.member.domain.event.PostAbuseReportApproveEvent;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.usecase.model.read.CommentAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.port.repository.ReportDashboardRepository;
import kr.modusplant.domains.member.usecase.port.repository.ReportRepository;
import kr.modusplant.domains.member.usecase.record.*;
import kr.modusplant.shared.exception.ExistsValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;

@SuppressWarnings("LoggingSimilarMessage")
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class MemberAdminController {
    private final MemberValidationHelper memberValidationHelper;
    private final ReportRepository reportRepository;
    private final ReportDashboardRepository reportDashboardRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public List<ProposalOrBugReportDashboardReadModel> getProposalOrBug(ProposalOrBugReportGetRecord record) {
        if (record.lastReportUlid() != null) {
            ReportId reportId = ReportId.create(record.lastReportUlid());
            memberValidationHelper.validateIfReportExists(reportId);
            return reportDashboardRepository.getProposalOrBugReports(ReportPageSize.create(record.size()), record.status(), reportId);
        } else {
            return reportDashboardRepository.getProposalOrBugReports(ReportPageSize.create(record.size()), record.status(), null);
        }
    }

    public ProposalOrBugReportDashboardReadModel checkProposalOrBug(ProposalOrBugReportCheckRecord record) {
        ReportId reportId = ReportId.create(record.reportUlid());
        memberValidationHelper.validateIfReportExists(reportId);
        if (reportRepository.isCheckedInProposalOrBugReport(reportId)) {
            throw new ExistsValueException(EXISTS_PROPOSAL_OR_BUG_REPORT_CHECKED, "checkedAt");
        }
        return reportDashboardRepository.checkProposalOrBugReport(reportId);
    }

    public void removeProposalOrBug(ProposalOrBugReportRemoveRecord record) {
        ReportId reportId = ReportId.create(record.reportUlid());
        if (reportRepository.isIdExistInProposalOrBugReport(reportId)) {
            reportRepository.removeProposalOrBugReport(reportId);
        }
    }

    public List<PostAbuseReportDashboardReadModel> getPostAbuseReport(PostAbuseReportGetRecord record) {
        if (record.lastPostUlid() != null) {
            ActivitySubjectPostId activitySubjectPostId = ActivitySubjectPostId.create(record.lastPostUlid());
            memberValidationHelper.validateIfActivitySubjectPostExists(activitySubjectPostId);
            return reportDashboardRepository.getPostAbuseReports(
                    ReportPageSize.create(record.size()), record.status(), activitySubjectPostId);
        } else {
            return reportDashboardRepository.getPostAbuseReports(
                    ReportPageSize.create(record.size()), record.status(), null);
        }
    }

    public PostAbuseReportDashboardReadModel dismissPostAbuse(PostAbuseReportDismissRecord record) {
        ActivitySubjectPostId postId = ActivitySubjectPostId.create(record.postUlid());
        memberValidationHelper.validateIfActivitySubjectPostExists(postId);
        if (reportDashboardRepository.isDismissedInPostAbuseReportDashboard(postId)) {
            throw new ExistsValueException(EXISTS_POST_ABUSE_REPORT_DISMISSED, "status");
        }
        return reportDashboardRepository.dismissPostAbuseReport(postId);
    }

    public PostAbuseReportDashboardReadModel approvePostAbuse(PostAbuseReportApproveRecord record) {
        ActivitySubjectPostId postId = ActivitySubjectPostId.create(record.postUlid());
        memberValidationHelper.validateIfActivitySubjectPostExists(postId);
        if (reportDashboardRepository.isApprovedInPostAbuseReportDashboard(postId)) {
            throw new ExistsValueException(EXISTS_POST_ABUSE_REPORT_BLINDED, "status");
        }
        applicationEventPublisher.publishEvent(PostAbuseReportApproveEvent.create(postId.getValue()));
        return reportDashboardRepository.approvePostAbuseReport(postId);
    }

    public List<CommentAbuseReportDashboardReadModel> getCommentAbuseReport(CommentAbuseReportGetRecord record) {
        if (!(record.lastPostUlid() == null && record.lastPath() == null)) {
            ActivitySubjectCommentId activitySubjectCommentId =
                    ActivitySubjectCommentId.create(
                            ActivitySubjectPostId.create(record.lastPostUlid()),
                            ActivitySubjectCommentPath.create(record.lastPath()));
            memberValidationHelper.validateIfActivitySubjectCommentExists(activitySubjectCommentId);
            return reportDashboardRepository.getCommentAbuseReports(
                    ReportPageSize.create(record.size()), record.status(), activitySubjectCommentId);
        } else {
            return reportDashboardRepository.getCommentAbuseReports(
                    ReportPageSize.create(record.size()), record.status(), null);
        }
    }

    public CommentAbuseReportDashboardReadModel dismissCommentAbuse(CommentAbuseReportDismissRecord record) {
        ActivitySubjectPostId postId = ActivitySubjectPostId.create(record.postUlid());
        ActivitySubjectCommentPath commentPath = ActivitySubjectCommentPath.create(record.path());
        ActivitySubjectCommentId commentId = ActivitySubjectCommentId.create(postId, commentPath);
        memberValidationHelper.validateIfActivitySubjectCommentExists(commentId);
        if (reportDashboardRepository.isDismissedInCommentAbuseReportDashboard(commentId)) {
            throw new ExistsValueException(EXISTS_COMMENT_ABUSE_REPORT_DISMISSED, "status");
        }
        return reportDashboardRepository.dismissCommentAbuseReport(commentId);
    }

    public CommentAbuseReportDashboardReadModel approveCommentAbuse(CommentAbuseReportApproveRecord record) {
        ActivitySubjectPostId postId = ActivitySubjectPostId.create(record.postUlid());
        ActivitySubjectCommentPath commentPath = ActivitySubjectCommentPath.create(record.path());
        ActivitySubjectCommentId commentId = ActivitySubjectCommentId.create(postId, commentPath);
        memberValidationHelper.validateIfActivitySubjectCommentExists(commentId);
        if (reportDashboardRepository.isApprovedInCommentAbuseReportDashboard(commentId)) {
            throw new ExistsValueException(EXISTS_COMMENT_ABUSE_REPORT_BLINDED, "status");
        }
        applicationEventPublisher.publishEvent(CommentAbuseReportApproveEvent.create(postId.getValue(), commentPath.getValue()));
        return reportDashboardRepository.approveCommentAbuseReport(commentId);
    }
}
