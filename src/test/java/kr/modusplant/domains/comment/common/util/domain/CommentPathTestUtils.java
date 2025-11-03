package kr.modusplant.domains.comment.common.util.domain;

import kr.modusplant.domains.comment.domain.vo.CommentPath;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;

public interface CommentPathTestUtils {
    CommentPath testCommentPath = CommentPath.create(TEST_COMM_COMMENT_PATH);
}
