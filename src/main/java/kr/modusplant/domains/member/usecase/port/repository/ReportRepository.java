package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.aggregate.ProposalOrBugReport;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;

public interface ReportRepository {
    boolean isIdExist(ReportId reportId);

    boolean isMemberAbusePost(MemberId memberId, TargetPostId targetPostId);

    boolean isMemberAbuseComment(MemberId memberId, TargetCommentId targetCommentId);

    void reportProposalOrBug(MemberId memberId, ProposalOrBugReport proposalOrBugReport);

    void reportPostAbuse(MemberId memberId, TargetPostId targetPostId);

    void reportCommentAbuse(MemberId memberId, TargetCommentId targetCommentId);

    void removeProposalOrBugReport(ReportId reportId);
}
