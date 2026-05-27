package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;
import kr.modusplant.domains.member.domain.vo.MemberId;

public interface ActivitySubjectPostRepository {
    boolean isIdExist(ActivitySubjectPostId activitySubjectPostId);

    boolean isPublished(ActivitySubjectPostId activitySubjectPostId);

    boolean isLiked(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    boolean isUnliked(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    boolean isBookmarked(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    boolean isNotBookmarked(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    void like(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    void unlike(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    void bookmark(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);

    void cancelBookmark(MemberId memberId, ActivitySubjectPostId activitySubjectPostId);
}
