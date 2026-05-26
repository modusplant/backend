package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.vo.ActivitySubjectCommentId;
import kr.modusplant.domains.member.domain.vo.MemberId;

public interface ActivitySubjectCommentRepository {
    boolean isIdExist(ActivitySubjectCommentId activitySubjectCommentId);

    boolean isLiked(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId);

    boolean isUnliked(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId);

    void like(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId);

    void unlike(MemberId memberId, ActivitySubjectCommentId activitySubjectCommentId);
}
