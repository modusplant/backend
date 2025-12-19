package kr.modusplant.domains.account.social.domain.exception;

import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidProviderIdException extends BusinessException {
    public InvalidProviderIdException() {
        super(SocialIdentityErrorCode.INVALID_PROVIDER_ID);
    }
}
