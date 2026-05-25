package kr.modusplant.domains.member.usecase.port.repository;

import jakarta.annotation.Nullable;
import kr.modusplant.domains.member.domain.aggregate.ProposalOrBugReport;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportAdminPageReadModel;

import java.util.List;

public interface ReportRepository {
    boolean isIdExistInProposalOrBugReport(ReportId reportId);

    boolean isCheckedInProposalOrBugReport(ReportId reportId);

    boolean isMemberAbusePost(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    boolean isMemberAbuseComment(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId);

    void reportProposalOrBug(MemberId memberId, ProposalOrBugReport proposalOrBugReport);

    void reportPostAbuse(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    void reportCommentAbuse(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId);

    void removeProposalOrBugReport(ReportId reportId);

    ProposalOrBugReportAdminPageReadModel checkProposalOrBugReport(ReportId reportId);

    List<ProposalOrBugReportAdminPageReadModel> getProposalOrBugReports(ReportPageSize reportPageSize, @Nullable ProposalOrBugReportStatus proposalOrBugReportStatus, @Nullable ReportId lastReportId);
}
