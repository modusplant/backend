package kr.modusplant.domains.communication.common.error;

import kr.modusplant.global.error.AccessDeniedDomainException;

public class PostAccessDeniedException extends AccessDeniedDomainException {
    public PostAccessDeniedException() {
        super("POST_ACCESS_DENIED", "게시글 접근이 거부되었습니다.");
    }
}
