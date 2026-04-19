package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;

public interface AbuseRepository {
    boolean isMemberAbusePost(MemberId memberId, TargetPostId targetPostId);

    boolean isMemberAbuseComment(MemberId memberId, TargetCommentId targetCommentId);
}
