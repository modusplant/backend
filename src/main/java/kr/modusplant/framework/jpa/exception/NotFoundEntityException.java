package kr.modusplant.framework.jpa.exception;

import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class NotFoundEntityException extends BusinessException {

    public NotFoundEntityException(EntityErrorCode errorCode) {
        super(errorCode);
    }
}
