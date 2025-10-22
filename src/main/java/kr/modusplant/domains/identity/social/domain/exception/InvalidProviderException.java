package kr.modusplant.domains.identity.social.domain.exception;

import kr.modusplant.domains.identity.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidProviderException extends BusinessException {
    public InvalidProviderException() {
        super(SocialIdentityErrorCode.INVALID_PROVIDER);
    }
}
