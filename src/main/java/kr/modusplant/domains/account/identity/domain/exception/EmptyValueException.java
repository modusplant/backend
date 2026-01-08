package kr.modusplant.domains.account.identity.domain.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.supers.ErrorCode;

public class EmptyValueException extends BusinessException {

    public EmptyValueException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EmptyValueException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
