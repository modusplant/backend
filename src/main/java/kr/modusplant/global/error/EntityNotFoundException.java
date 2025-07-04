package kr.modusplant.global.error;

import kr.modusplant.global.enums.ErrorCode;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends BusinessException{
    private final String entityName;

    public EntityNotFoundException(ErrorCode errorCode, String entityName) {
        super(errorCode);
        this.entityName = entityName;
    }
}
