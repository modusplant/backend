package kr.modusplant.domains.identity.social.domain.exception;

import kr.modusplant.domains.identity.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidMemberIdException extends BusinessException {
    public InvalidMemberIdException() {
        super(SocialIdentityErrorCode.INVALID_MEMBER_ID);
    }
}
