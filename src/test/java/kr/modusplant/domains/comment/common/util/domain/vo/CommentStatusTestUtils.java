package kr.modusplant.domains.comment.common.util.domain.vo;

import kr.modusplant.domains.comment.domain.vo.CommentStatus;

public interface CommentStatusTestUtils {
    CommentStatus testCommentStatusValid = CommentStatus.active();
    CommentStatus testCommentStatusDeleted = CommentStatus.deleted();
}
