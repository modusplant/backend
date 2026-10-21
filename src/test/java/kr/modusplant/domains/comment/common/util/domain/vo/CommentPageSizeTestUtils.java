package kr.modusplant.domains.comment.common.util.domain.vo;

import kr.modusplant.domains.comment.domain.vo.CommentPageSize;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PAGE_SIZE;

public interface CommentPageSizeTestUtils {
    CommentPageSize testCommentPageSize = CommentPageSize.create(TEST_COMMENT_PAGE_SIZE);
}
