package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.supers.ErrorCode;

/**
 * 클라이언트 요청의 필드 값이 비었기 때문에 요청을 처리할 수 없는 경우에 사용합니다.
 * 필드의 값이 null, "", [] 인 경우 등이 해당합니다.
 */
public class EmptyValueException extends BusinessException {

    private final String valueName;

    public EmptyValueException(ErrorCode errorCode, String valueName) {
        super(errorCode);
        this.valueName = valueName;
    }

    public EmptyValueException(ErrorCode errorCode, String valueName, String message) {
        super(errorCode, message);
        this.valueName = valueName;

    }

    public EmptyValueException(ErrorCode errorCode, String valueName, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.valueName = valueName;
    }

    public EmptyValueException(ErrorCode errorCode, String valueName, Throwable cause) {
        super(errorCode, cause);
        this.valueName = valueName;
    }

    @Override
    public String getMessage() {
        return String.format("%s [valueName: %s]", super.getMessage(), valueName);
    }
}
