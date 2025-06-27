package kr.modusplant.domains.communication.common.error;

import kr.modusplant.global.error.EntityExistsDomainException;

public class CommentExistsException extends EntityExistsDomainException {
    public CommentExistsException() {
        super("COMMENT_EXISTS", "댓글이 이미 존재합니다.");
    }
}
