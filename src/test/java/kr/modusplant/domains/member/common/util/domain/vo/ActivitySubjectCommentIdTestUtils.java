package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ActivitySubjectCommentId;

import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectCommentPathTestUtils.testActivitySubjectCommentPath;
import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectPostIdTestUtils.testActivitySubjectPostId;

public interface ActivitySubjectCommentIdTestUtils {
    ActivitySubjectCommentId testActivitySubjectCommentId = ActivitySubjectCommentId.create(testActivitySubjectPostId, testActivitySubjectCommentPath);
}
