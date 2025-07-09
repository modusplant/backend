package kr.modusplant.domains.common.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;

public class InvalidMultipartDataException extends BusinessException {
    public InvalidMultipartDataException(ErrorCode errorCode) {
        super(errorCode);
    }
}
