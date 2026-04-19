package kr.modusplant.domains.account.social.domain.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.supers.ErrorCode;

public class AlreadyRegisteredWithOtherProviderException extends BusinessException {
    public AlreadyRegisteredWithOtherProviderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
