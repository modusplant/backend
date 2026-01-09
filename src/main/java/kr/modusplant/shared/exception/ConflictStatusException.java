package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.supers.ErrorCode;

public class ConflictStatusException extends BusinessException {

    public ConflictStatusException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConflictStatusException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ConflictStatusException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public ConflictStatusException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
