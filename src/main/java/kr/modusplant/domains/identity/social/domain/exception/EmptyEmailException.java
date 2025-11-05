package kr.modusplant.domains.identity.social.domain.exception;

import kr.modusplant.domains.identity.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyEmailException extends BusinessException {
    public EmptyEmailException() {
        super(SocialIdentityErrorCode.EMPTY_EMAIL);
    }
}
