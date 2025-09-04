package kr.modusplant.domains.comment.support.utils.domain;

import kr.modusplant.domains.comment.domain.vo.CommentStatus;

public interface CommentStatusTestUtils {
    CommentStatus testCommentStatusValid = CommentStatus.setAsValid();
    CommentStatus testCommentStatusDeleted = CommentStatus.setAsDeleted();
}
