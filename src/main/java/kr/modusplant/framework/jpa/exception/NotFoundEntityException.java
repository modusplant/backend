package kr.modusplant.framework.jpa.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.supers.ErrorCode;

public class NotFoundEntityException extends BusinessException {

    private final String entityName;

    public NotFoundEntityException(ErrorCode errorCode, String entityName) {
        super(errorCode);
        this.entityName = entityName;
    }

    public NotFoundEntityException(ErrorCode errorCode, String entityName, Throwable cause) {
        super(errorCode, cause);
        this.entityName = entityName;
    }

    public NotFoundEntityException(ErrorCode errorCode, String entityName, String message) {
        super(errorCode, message);
        this.entityName = entityName;
    }

    public NotFoundEntityException(ErrorCode errorCode, String entityName, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.entityName = entityName;
    }

    @Override
    public String getMessage() {
        return String.format("%s [entityName: %s]", super.getMessage(), entityName);
    }
}
