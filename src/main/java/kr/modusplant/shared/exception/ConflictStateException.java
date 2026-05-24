package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.supers.ErrorCode;

public class ConflictStateException extends BusinessException {

    public ConflictStateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConflictStateException(ErrorCode errorCode, String message) {
        super(errorCode);

    }

    public ConflictStateException(ErrorCode errorCode, String valueName, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public ConflictStateException(ErrorCode errorCode, String valueName, Throwable cause) {
        super(errorCode, cause);
    }

    @Override
    public String getMessage() {
        return String.format(super.getMessage());
    }
}
