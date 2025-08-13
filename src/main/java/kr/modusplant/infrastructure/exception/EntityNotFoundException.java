package kr.modusplant.infrastructure.exception;

import kr.modusplant.infrastructure.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends BusinessException{
    private final String entityName;

    public EntityNotFoundException(ErrorCode errorCode, String entityName) {
        super(errorCode);
        this.entityName = entityName;
    }
}
