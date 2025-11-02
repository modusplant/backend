package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import lombok.Getter;

@Getter
public class EntityExistsException extends BusinessException {

    private final String entityName;

    public EntityExistsException(ResponseCode errorCode, String entityName) {
        super(errorCode);
        this.entityName = entityName;
    }
}
