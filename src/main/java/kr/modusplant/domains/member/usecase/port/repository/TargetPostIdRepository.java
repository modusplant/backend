package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;

public interface TargetPostIdRepository {
    boolean isIdExist(TargetPostId targetPostId);

    boolean isLiked(MemberId memberId, TargetPostId targetPostId);

    boolean isUnliked(MemberId memberId, TargetPostId targetPostId);
}
