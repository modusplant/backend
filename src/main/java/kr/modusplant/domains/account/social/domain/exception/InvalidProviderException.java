package kr.modusplant.domains.account.social.domain.exception;

import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidProviderException extends BusinessException {
    public InvalidProviderException() {
        super(SocialIdentityErrorCode.INVALID_PROVIDER);
    }
}
