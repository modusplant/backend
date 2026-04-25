package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;

public interface ReportRepository {
    boolean isIdExist(ReportId reportId);

    boolean isMemberAbusePost(MemberId memberId, TargetPostId targetPostId);

    boolean isMemberAbuseComment(MemberId memberId, TargetCommentId targetCommentId);
}
