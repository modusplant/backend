package kr.modusplant.domains.comment.domain.exception;

import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class DeletedReplyToCommentException extends BusinessException {
    public DeletedReplyToCommentException() {
        super(CommentErrorCode.DELETED_REPLY_TO_COMMENT);
    }
}
