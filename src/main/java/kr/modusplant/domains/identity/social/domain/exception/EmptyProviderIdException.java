package kr.modusplant.domains.identity.social.domain.exception;

import kr.modusplant.domains.identity.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyProviderIdException extends BusinessException {
    public EmptyProviderIdException() {
        super(SocialIdentityErrorCode.EMPTY_PROVIDER_ID);
    }
}
