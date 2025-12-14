package kr.modusplant.domains.account.normal.domain.exception;

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
