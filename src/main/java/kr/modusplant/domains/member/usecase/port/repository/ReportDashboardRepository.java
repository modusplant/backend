package kr.modusplant.domains.member.usecase.port.repository;

import jakarta.annotation.Nullable;
import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.usecase.model.read.CommentAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;

import java.util.List;

/**
 * {@code ReportDashboardRepository}는 관리자가 대시보드에서 보고를 관리하는 활동과 강한 연관성이 있는 기능을 다루는 인터페이스입니다.
 *
 * <p>대시보드 관련 객체를 다루거나, 대시보드에서의 상호 작용을 다룬다고 할 수도 있습니다.</p>
 *
 * @author Jun Hyeok
 */
public interface ReportDashboardRepository {
    List<ProposalOrBugReportDashboardReadModel> getProposalOrBugReports(
            ReportPageSize reportPageSize,
            @Nullable ProposalOrBugReportStatus proposalOrBugReportStatus,
            @Nullable ReportId lastReportId);

    ProposalOrBugReportDashboardReadModel checkProposalOrBugReport(ReportId reportId);

    List<PostAbuseReportDashboardReadModel> getPostAbuseReports(
            ReportPageSize reportPageSize,
            @Nullable AbuseReportStatus status,
            @Nullable String lastPostUlid);

    void reflectPostAbuseReport(ActivitySubjectPostId postId, ReportTime reportTime);

    PostAbuseReportDashboardReadModel dismissPostAbuseReport(ActivitySubjectPostId postId);

    PostAbuseReportDashboardReadModel approvePostAbuseReport(ActivitySubjectPostId postId);

    boolean isDismissedInPostAbuseReportDashboard(ActivitySubjectPostId postId);

    boolean isApprovedInPostAbuseReportDashboard(ActivitySubjectPostId postId);

    List<CommentAbuseReportDashboardReadModel> getCommentAbuseReports(
            ReportPageSize reportPageSize,
            @Nullable AbuseReportStatus status,
            @Nullable String lastPostUlid,
            @Nullable String lastPath);

    void reflectCommentAbuseReport(ActivitySubjectCommentId commentId, ReportTime reportTime);

    CommentAbuseReportDashboardReadModel dismissCommentAbuseReport(ActivitySubjectCommentId commentId);

    CommentAbuseReportDashboardReadModel approveCommentAbuseReport(ActivitySubjectCommentId commentId);

    boolean isDismissedInCommentAbuseReportDashboard(ActivitySubjectCommentId commentId);

    boolean isApprovedInCommentAbuseReportDashboard(ActivitySubjectCommentId commentId);
}
