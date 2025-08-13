package kr.modusplant.global.error;

import kr.modusplant.domain.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class EntityExistsException extends BusinessException {

    private final String entityName;

    public EntityExistsException(ErrorCode errorCode, String entityName) {
        super(errorCode);
        this.entityName = entityName;
    }
}
