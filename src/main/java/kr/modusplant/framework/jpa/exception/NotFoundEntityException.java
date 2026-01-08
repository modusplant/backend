package kr.modusplant.framework.jpa.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;

public class NotFoundEntityException extends BusinessException {

    public NotFoundEntityException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public NotFoundEntityException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public NotFoundEntityException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public NotFoundEntityException(ErrorCode errorCode) {
        super(errorCode);
    }
}
