package kr.modusplant.legacy.modules.auth.social.error;

import kr.modusplant.global.error.BusinessException;
import kr.modusplant.infrastructure.exception.enums.ErrorCode;

public class UnsupportedSocialProviderException extends BusinessException {
    public UnsupportedSocialProviderException() {
        super(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
    }
}
