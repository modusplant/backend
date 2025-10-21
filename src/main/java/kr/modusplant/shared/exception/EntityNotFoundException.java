package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends BusinessException{
    private final String entityName;

    public EntityNotFoundException(ResponseCode errorCode, String entityName) {
        super(errorCode);
        this.entityName = entityName;
    }
}
