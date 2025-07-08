package kr.modusplant.domains.communication.error;

import kr.modusplant.global.error.EntityNotFoundDomainException;

public class LikeNotFoundException extends EntityNotFoundDomainException {
    public LikeNotFoundException() {
        super("LIKE_NOT_FOUND", "좋아요를 누르지 않았습니다.");
    }
}
