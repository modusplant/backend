package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.supers.ErrorCode;

/**
 * 사용하려는 값이 아직 초기화되지 않은 경우에 발생하는 예외입니다.
 * 빈 초기화 이전에 홀더의 정적 필드에 접근하는 경우 등이 해당됩니다.
 */
public class NotInitializedException extends BusinessException {

    private final String valueName;

    public NotInitializedException(ErrorCode errorCode, String valueName) {
        super(errorCode);
        this.valueName = valueName;
    }

    public NotInitializedException(ErrorCode errorCode, String valueName, String message) {
        super(errorCode, message);
        this.valueName = valueName;

    }

    public NotInitializedException(ErrorCode errorCode, String valueName, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.valueName = valueName;
    }

    public NotInitializedException(ErrorCode errorCode, String valueName, Throwable cause) {
        super(errorCode, cause);
        this.valueName = valueName;
    }

    @Override
    public String getMessage() {
        return String.format("%s [valueName: %s]", super.getMessage(), valueName);
    }
}
