package kr.modusplant.domains.communication.error;

import kr.modusplant.global.error.EntityNotFoundDomainException;

public class PostNotFoundException extends EntityNotFoundDomainException {
    public PostNotFoundException() {
        super("POST_NOT_FOUND", "게시글을 찾을 수 없습니다.");
    }
}
