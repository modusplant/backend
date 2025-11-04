package kr.modusplant.domains.identity.social.framework.out.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;

public class UnsupportedSocialProviderException extends BusinessException {
    public UnsupportedSocialProviderException() {
        super(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
    }
}
