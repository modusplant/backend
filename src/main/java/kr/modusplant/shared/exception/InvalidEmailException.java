package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.ErrorCode;

public class InvalidEmailException extends BusinessException {
    public InvalidEmailException() {
        super(ErrorCode.INVALID_EMAIL);
    }
}
