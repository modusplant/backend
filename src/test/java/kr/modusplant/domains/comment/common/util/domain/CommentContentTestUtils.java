package kr.modusplant.domains.comment.common.util.domain;

import kr.modusplant.domains.comment.domain.vo.CommentContent;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_CONTENT;

public interface CommentContentTestUtils {
    CommentContent testCommentContent = CommentContent.create(TEST_COMM_COMMENT_CONTENT);
}
