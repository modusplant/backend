package kr.modusplant.domains.communication.error;

import kr.modusplant.global.error.EntityExistsDomainException;

public class LikeExistsException extends EntityExistsDomainException {
    public LikeExistsException() {
        super("LIKE_EXISTS", "이미 좋아요를 눌렀습니다.");
    }
}
