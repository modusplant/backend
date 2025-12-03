package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.ErrorCode;

public class EmptyEmailException extends BusinessException {
    public EmptyEmailException() {
        super(ErrorCode.EMAIL_EMPTY);
    }
}
