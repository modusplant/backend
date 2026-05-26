package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ActivitySubjectCommentPath;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;

public interface ActivitySubjectCommentPathTestUtils {
    ActivitySubjectCommentPath testActivitySubjectCommentPath = ActivitySubjectCommentPath.create(TEST_COMM_COMMENT_PATH);
}
