package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.supers.ErrorCode;

/**
 * 외부 SW 또는 API와의 통신이 실패할 때 발생하는 예외입니다.
 * Redis, PostgreSQL 등과 통신할 때 서버 다운, 네트워크 장애 등을 겪는 경우가 해당됩니다.
 */
public class ConnectionFailedException extends BusinessException {

    private final String valueName;

    public ConnectionFailedException(ErrorCode errorCode, String valueName) {
        super(errorCode);
        this.valueName = valueName;
    }

    public ConnectionFailedException(ErrorCode errorCode, String valueName, String message) {
        super(errorCode, message);
        this.valueName = valueName;

    }

    public ConnectionFailedException(ErrorCode errorCode, String valueName, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.valueName = valueName;
    }

    public ConnectionFailedException(ErrorCode errorCode, String valueName, Throwable cause) {
        super(errorCode, cause);
        this.valueName = valueName;
    }

    @Override
    public String getMessage() {
        return String.format("%s [valueName: %s]", super.getMessage(), valueName);
    }
}
