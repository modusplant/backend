package kr.modusplant.domains.identity.social.domain.exception;

import kr.modusplant.domains.identity.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyProviderException extends BusinessException {
    public EmptyProviderException() {
        super(SocialIdentityErrorCode.EMPTY_PROVIDER);
    }
}
