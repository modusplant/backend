package kr.modusplant.framework.jpa.exception;

import kr.modusplant.framework.jpa.exception.enums.EntityName;
import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.supers.ResponseCode;

public class ExistsEntityException extends BusinessException {

    private final EntityName entityName;

    public ExistsEntityException(ResponseCode errorCode, EntityName entityName) {
        super(errorCode);
        this.entityName = entityName;
    }

    public ExistsEntityException(ResponseCode errorCode, EntityName entityName, String message) {
        super(errorCode, message);
        this.entityName = entityName;
    }

    public ExistsEntityException(ResponseCode errorCode, EntityName entityName, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.entityName = entityName;
    }

    public ExistsEntityException(ResponseCode errorCode, EntityName entityName, Throwable cause) {
        super(errorCode, cause);
        this.entityName = entityName;
    }

    @Override
    public String getMessage() {
        return String.format("%s [entityName: %s]", super.getMessage(), entityName);
    }
}
