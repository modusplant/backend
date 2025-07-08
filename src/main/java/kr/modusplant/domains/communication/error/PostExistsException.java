package kr.modusplant.domains.communication.error;

import kr.modusplant.global.error.EntityExistsDomainException;

public class PostExistsException extends EntityExistsDomainException {
    public PostExistsException() {
        super("POST_EXISTS", "게시글이 이미 존재합니다.");
    }
}
