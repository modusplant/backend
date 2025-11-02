package kr.modusplant.domains.identity.normal.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.supers.ResponseCode;

public class DataAlreadyExistsException extends BusinessException {

    public DataAlreadyExistsException(ResponseCode errorCode) {
        super(errorCode);
    }

    public DataAlreadyExistsException(ResponseCode errorCode, String message) {
        super(errorCode, message);
    }
}
