package kr.modusplant.domains.comment.common.util.domain;

import kr.modusplant.domains.comment.domain.vo.CommentPath;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_REPLY_TO_PATH;

public interface CommentPathTestUtils {
    CommentPath testCommentPath = CommentPath.create(TEST_COMMENT_PATH);
    CommentPath testCommentReplyToPath = CommentPath.create(TEST_COMMENT_REPLY_TO_PATH);
    CommentPath testCommentRootPath = CommentPath.create("1");
}
