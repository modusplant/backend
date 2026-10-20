package kr.modusplant.domains.comment.common.util.domain.vo;

import kr.modusplant.domains.comment.domain.vo.ReplyToCommentPath;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_REPLY_TO_PATH;

public interface ReplyToCommentPathTestUtils {
    ReplyToCommentPath testReplyToCommentPath = ReplyToCommentPath.create(TEST_COMMENT_REPLY_TO_PATH);
}
