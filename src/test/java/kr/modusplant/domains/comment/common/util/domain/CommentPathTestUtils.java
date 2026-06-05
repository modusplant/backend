package kr.modusplant.domains.comment.common.util.domain;

import kr.modusplant.domains.comment.domain.vo.CommentPath;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;

public interface CommentPathTestUtils {
    CommentPath testCommentPath = CommentPath.create(TEST_COMMENT_PATH);
}
