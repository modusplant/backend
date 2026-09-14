package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.aggregate.ProposalOrBugReport;
import kr.modusplant.domains.member.domain.vo.*;

/**
 * {@code ReportRepository}는 유저로부터 온 보고를 다루는 인터페이스입니다.
 *
 * <p>대시보드에서의 보고 관리 활동과 무관한, 보고에 관한 단순한 기능을 다룬다고 할 수도 있습니다.</p>
 *
 * @author Jun Hyeok
 */
public interface ReportRepository {
    boolean isIdExistInProposalOrBugReport(ReportId reportId);

    boolean isUncheckedInProposalOrBugReport(ReportId reportId);

    boolean isCheckedInProposalOrBugReport(ReportId reportId);

    boolean isMemberAbusePost(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    boolean isMemberAbuseComment(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId);

    void reportProposalOrBug(MemberId memberId, ProposalOrBugReport proposalOrBugReport);

    ReportTime reportPostAbuse(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    ReportTime reportCommentAbuse(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId);

    void removeProposalOrBugReport(ReportId reportId);
}
