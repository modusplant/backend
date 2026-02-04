package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends BusinessException{
    private final String entityName;

    public EntityNotFoundException(ErrorCode errorCode, String entityName) {
        super(errorCode);
        this.entityName = entityName;
    }
}
