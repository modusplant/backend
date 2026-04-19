package kr.modusplant.domains.account.social.domain.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.supers.ErrorCode;

public class SocialActionRequiredException extends BusinessException {
    public SocialActionRequiredException(ErrorCode errorCode) {
        super(errorCode);
    }
}
