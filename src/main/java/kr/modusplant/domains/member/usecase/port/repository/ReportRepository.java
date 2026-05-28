package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.aggregate.ProposalOrBugReport;
import kr.modusplant.domains.member.domain.vo.*;

public interface ReportRepository {
    boolean isIdExistInProposalOrBugReport(ReportId reportId);

    boolean isCheckedInProposalOrBugReport(ReportId reportId);

    boolean isMemberAbusePost(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    boolean isMemberAbuseComment(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId);

    void reportProposalOrBug(MemberId memberId, ProposalOrBugReport proposalOrBugReport);

    ReportTime reportPostAbuse(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    void reportCommentAbuse(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId);

    void removeProposalOrBugReport(ReportId reportId);
}
