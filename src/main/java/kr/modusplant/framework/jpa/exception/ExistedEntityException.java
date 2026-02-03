package kr.modusplant.framework.jpa.exception;

import kr.modusplant.framework.jpa.exception.enums.EntityName;
import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.supers.ErrorCode;

public class ExistedEntityException extends BusinessException {

    private final EntityName entityName;

    public ExistedEntityException(ErrorCode errorCode, EntityName entityName) {
        super(errorCode);
        this.entityName = entityName;
    }

    public ExistedEntityException(ErrorCode errorCode, EntityName entityName, String message) {
        super(errorCode, message);
        this.entityName = entityName;
    }

    public ExistedEntityException(ErrorCode errorCode, EntityName entityName, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.entityName = entityName;
    }

    public ExistedEntityException(ErrorCode errorCode, EntityName entityName, Throwable cause) {
        super(errorCode, cause);
        this.entityName = entityName;
    }

    @Override
    public String getMessage() {
        return String.format("%s [entityName: %s]", super.getMessage(), entityName);
    }
}
