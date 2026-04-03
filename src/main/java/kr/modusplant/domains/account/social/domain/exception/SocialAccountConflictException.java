package kr.modusplant.domains.account.social.domain.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.supers.ErrorCode;

public class SocialAccountConflictException extends BusinessException {
    public SocialAccountConflictException(ErrorCode errorCode) {
        super(errorCode);
    }
}
