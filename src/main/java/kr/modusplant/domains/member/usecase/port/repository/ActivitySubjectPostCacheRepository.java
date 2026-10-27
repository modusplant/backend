package kr.modusplant.domains.member.usecase.port.repository;

import kr.modusplant.domains.member.domain.vo.ActivitySubjectCommentId;

public interface ActivitySubjectPostCacheRepository {
    boolean hasChosenCommentWithDifferentPath(ActivitySubjectCommentId activitySubjectCommentId);

    void cacheBeforeChoose(ActivitySubjectCommentId activitySubjectCommentId);

    void removeChooseCache(ActivitySubjectCommentId activitySubjectCommentId);
}
