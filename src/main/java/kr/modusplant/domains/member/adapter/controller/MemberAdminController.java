package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.domain.event.CommentAbuseReportApproveEvent;
import kr.modusplant.domains.member.domain.event.CommentAbuseReportDismissEvent;
import kr.modusplant.domains.member.domain.event.PostAbuseReportApproveEvent;
import kr.modusplant.domains.member.domain.event.PostAbuseReportDismissEvent;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.usecase.model.read.CommentAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.port.repository.ReportDashboardRepository;
import kr.modusplant.domains.member.usecase.port.repository.ReportRepository;
import kr.modusplant.domains.member.usecase.record.*;
import kr.modusplant.domains.member.usecase.response.CommentAbuseReportDashboardResponse;
import kr.modusplant.domains.member.usecase.response.PostAbuseReportDashboardResponse;
import kr.modusplant.domains.member.usecase.response.ProposalOrBugReportDashboardResponse;
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

    public ProposalOrBugReportDashboardResponse getProposalOrBug(ProposalOrBugReportGetRecord record) {
        int reportPageSize = record.size() + 1;
        List<ProposalOrBugReportDashboardReadModel> readModels;
        if (record.lastReportUlid() != null) { // 특정 lastReportId 이후부터 조회
            ReportId reportId = ReportId.create(record.lastReportUlid());
            memberValidationHelper.validateIfReportExists(reportId);
            readModels = reportDashboardRepository.getProposalOrBugReports(
                    ReportPageSize.create(reportPageSize), record.status(), reportId);
        } else { // 첫 보고서 데이터부터 조회
            readModels = reportDashboardRepository.getProposalOrBugReports(
                    ReportPageSize.create(reportPageSize), record.status(), null);
        }

        if (readModels.size() == reportPageSize) { // hasNext == true
            List<ProposalOrBugReportDashboardReadModel> returnedReadModels =
                    readModels.subList(0, reportPageSize - 1);
            return ProposalOrBugReportDashboardResponse.of(
                    returnedReadModels, returnedReadModels.getLast().ulid(), true);
        } else { // hasNext == false
            if (!readModels.isEmpty()) {
                return ProposalOrBugReportDashboardResponse.of(
                        readModels, readModels.getLast().ulid(), false);
            } else {
                return ProposalOrBugReportDashboardResponse.of(
                        readModels, null, false);
            }
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
            if (reportRepository.isUncheckedInProposalOrBugReport(reportId)) {
                throw new ExistsValueException(NOT_FOUND_PROPOSAL_OR_BUG_REPORT_CHECKED, "checkedAt");
            }
            reportRepository.removeProposalOrBugReport(reportId);
        }
    }

    public PostAbuseReportDashboardResponse getPostAbuseReport(PostAbuseReportGetRecord record) {
        int reportPageSize = record.size() + 1;
        List<PostAbuseReportDashboardReadModel> readModels;
        if (record.lastPostUlid() != null) {
            ActivitySubjectPostId activitySubjectPostId = ActivitySubjectPostId.create(record.lastPostUlid());
            memberValidationHelper.validateIfActivitySubjectPostExists(activitySubjectPostId);
            readModels = reportDashboardRepository.getPostAbuseReports(
                    ReportPageSize.create(reportPageSize), record.status(), activitySubjectPostId);
        } else {
            readModels = reportDashboardRepository.getPostAbuseReports(
                    ReportPageSize.create(reportPageSize), record.status(), null);
        }

        if (readModels.size() == reportPageSize) { // hasNext == true
            List<PostAbuseReportDashboardReadModel> returnedReadModels =
                    readModels.subList(0, reportPageSize - 1);
            return PostAbuseReportDashboardResponse.of(
                    returnedReadModels, returnedReadModels.getLast().ulid(), true);
        } else { // hasNext == false
            if (!readModels.isEmpty()) {
                return PostAbuseReportDashboardResponse.of(
                        readModels, readModels.getLast().ulid(), false);
            } else {
                return PostAbuseReportDashboardResponse.of(
                        readModels, null, false);
            }
        }
    }

    public PostAbuseReportDashboardReadModel dismissPostAbuse(PostAbuseReportDismissRecord record) {
        ActivitySubjectPostId postId = ActivitySubjectPostId.create(record.postUlid());
        memberValidationHelper.validateIfActivitySubjectPostExists(postId);
        if (reportDashboardRepository.isDismissedInPostAbuseReportDashboard(postId)) {
            throw new ExistsValueException(EXISTS_POST_ABUSE_REPORT_DISMISSED, "status");
        }
        applicationEventPublisher.publishEvent(PostAbuseReportDismissEvent.create(postId.getValue()));
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

    public CommentAbuseReportDashboardResponse getCommentAbuseReport(CommentAbuseReportGetRecord record) {
        int reportPageSize = record.size() + 1;
        List<CommentAbuseReportDashboardReadModel> readModels;
        if (!(record.lastPostUlid() == null && record.lastPath() == null)) {
            ActivitySubjectCommentId activitySubjectCommentId =
                    ActivitySubjectCommentId.create(
                            ActivitySubjectPostId.create(record.lastPostUlid()),
                            ActivitySubjectCommentPath.create(record.lastPath()));
            memberValidationHelper.validateIfActivitySubjectCommentExists(activitySubjectCommentId);
            readModels = reportDashboardRepository.getCommentAbuseReports(
                    ReportPageSize.create(reportPageSize), record.status(), activitySubjectCommentId);
        } else {
            readModels = reportDashboardRepository.getCommentAbuseReports(
                    ReportPageSize.create(reportPageSize), record.status(), null);
        }

        if (readModels.size() == reportPageSize) { // hasNext == true
            List<CommentAbuseReportDashboardReadModel> returnedReadModels =
                    readModels.subList(0, reportPageSize - 1);
            return CommentAbuseReportDashboardResponse.of(
                    returnedReadModels,
                    returnedReadModels.getLast().postUlid(),
                    returnedReadModels.getLast().path(),
                    true);
        } else { // hasNext == false
            if (!readModels.isEmpty()) {
                return CommentAbuseReportDashboardResponse.of(
                        readModels, readModels.getLast().postUlid(), readModels.getLast().path(), false);
            } else {
                return CommentAbuseReportDashboardResponse.of(
                        readModels, null, null, false);
            }
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
        applicationEventPublisher.publishEvent(
                CommentAbuseReportDismissEvent.create(postId.getValue(), commentPath.getValue()));
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
        applicationEventPublisher.publishEvent(
                CommentAbuseReportApproveEvent.create(postId.getValue(), commentPath.getValue()));
        return reportDashboardRepository.approveCommentAbuseReport(commentId);
    }
}
