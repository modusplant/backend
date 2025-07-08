package kr.modusplant.modules.auth.social.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;

public class UnsupportedSocialProviderException extends BusinessException {
    public UnsupportedSocialProviderException() {
        super(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
    }
}
