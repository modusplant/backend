package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.ErrorCode;

public class EmptyPasswordException extends BusinessException {
    public EmptyPasswordException() {
        super(ErrorCode.PASSWORD_EMPTY);
    }
}
