package kr.modusplant.domains.comment.common.util.domain;

import kr.modusplant.domains.comment.domain.vo.CommentContent;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_CONTENT;

public interface CommentContentTestUtils {
    CommentContent testCommentContent = CommentContent.create(TEST_COMM_COMMENT_CONTENT);
}
