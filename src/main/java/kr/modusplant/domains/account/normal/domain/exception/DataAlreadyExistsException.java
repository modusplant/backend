package kr.modusplant.domains.account.normal.domain.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.supers.ErrorCode;

public class DataAlreadyExistsException extends BusinessException {

    public DataAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DataAlreadyExistsException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
