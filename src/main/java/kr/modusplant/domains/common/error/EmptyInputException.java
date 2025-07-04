package kr.modusplant.domains.common.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;

public class EmptyInputException extends BusinessException {
    public EmptyInputException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static EmptyInputException postUlid() {
        return new EmptyInputException(ErrorCode.EMPTY_POSTULID_INPUT);
    }

    public static EmptyInputException path() {
        return new EmptyInputException(ErrorCode.EMPTY_PATH_INPUT);
    }
}
