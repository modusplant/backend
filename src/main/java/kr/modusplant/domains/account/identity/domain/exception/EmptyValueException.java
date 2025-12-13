package kr.modusplant.domains.account.identity.domain.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.supers.ResponseCode;

public class EmptyValueException extends BusinessException {

    public EmptyValueException(ResponseCode errorCode) {
        super(errorCode);
    }

    public EmptyValueException(ResponseCode errorCode, String message) {
        super(errorCode, message);
    }
}
