package kr.modusplant.domains.account.social.framework.out.exception;

import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class UnsupportedSocialProviderException extends BusinessException {
    public UnsupportedSocialProviderException() {
        super(SocialIdentityErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
    }
}
