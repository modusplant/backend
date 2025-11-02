package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;

public interface TargetCommentIdRepository {
    boolean isIdExist(TargetCommentId targetCommentId);

    boolean isLiked(MemberId memberId, TargetCommentId targetCommentId);

    boolean isUnliked(MemberId memberId, TargetCommentId targetCommentId);
}
