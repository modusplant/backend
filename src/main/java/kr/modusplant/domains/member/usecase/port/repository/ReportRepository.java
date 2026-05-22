package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.aggregate.ProposalOrBugReport;
import kr.modusplant.domains.member.domain.vo.ActivitySubjectCommentId;
import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.ReportId;

public interface ReportRepository {
    boolean isIdExistInProposalOrBugReport(ReportId reportId);

    boolean isCheckedInProposalOrBugReport(ReportId reportId);

    boolean isMemberAbusePost(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    boolean isMemberAbuseComment(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId);

    void reportProposalOrBug(MemberId memberId, ProposalOrBugReport proposalOrBugReport);

    void reportPostAbuse(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    void reportCommentAbuse(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId);

    void checkProposalOrBugReport(ReportId reportId);

    void removeProposalOrBugReport(ReportId reportId);
}
