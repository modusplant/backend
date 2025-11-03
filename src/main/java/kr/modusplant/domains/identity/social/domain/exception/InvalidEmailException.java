package kr.modusplant.domains.identity.social.domain.exception;

import kr.modusplant.domains.identity.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidEmailException extends BusinessException {
    public InvalidEmailException() {
        super(SocialIdentityErrorCode.INVALID_EMAIL);
    }
}
