package kr.modusplant.framework.jpa.exception;

import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class ExistsEntityException extends BusinessException {

    public ExistsEntityException(EntityErrorCode responseCode) {
        super(responseCode);
    }
}
