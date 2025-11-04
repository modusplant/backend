package kr.modusplant.domains.identity.social.domain.exception;

import kr.modusplant.domains.identity.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyMemberIdException extends BusinessException {
    public EmptyMemberIdException() {
        super(SocialIdentityErrorCode.EMPTY_MEMBER_ID);
    }
}
