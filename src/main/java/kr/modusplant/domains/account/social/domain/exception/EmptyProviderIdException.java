package kr.modusplant.domains.account.social.domain.exception;

import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyProviderIdException extends BusinessException {
    public EmptyProviderIdException() {
        super(SocialIdentityErrorCode.EMPTY_PROVIDER_ID);
    }
}
