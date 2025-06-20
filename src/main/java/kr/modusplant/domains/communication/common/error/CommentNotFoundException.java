package kr.modusplant.domains.communication.common.error;

import kr.modusplant.global.error.EntityNotFoundDomainException;

public class CommentNotFoundException extends EntityNotFoundDomainException {
    public CommentNotFoundException() {
        super("COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다.");
    }
}
