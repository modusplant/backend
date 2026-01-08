package kr.modusplant.framework.jpa.exception;

import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class NotFoundEntityException extends BusinessException {

    public NotFoundEntityException(EntityErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public NotFoundEntityException(EntityErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public NotFoundEntityException(EntityErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public NotFoundEntityException(EntityErrorCode errorCode) {
        super(errorCode);
    }
}
