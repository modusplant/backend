package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;

public class InvalidValueException extends BusinessException {

    private final String valueName;

    public InvalidValueException(ResponseCode errorCode, String valueName) {
        super(errorCode);
        this.valueName = valueName;
    }

    public InvalidValueException(ResponseCode errorCode, String valueName, String message) {
        super(errorCode, message);
        this.valueName = valueName;

    }

    public InvalidValueException(ResponseCode errorCode, String valueName, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.valueName = valueName;
    }

    public InvalidValueException(ResponseCode errorCode, String valueName, Throwable cause) {
        super(errorCode, cause);
        this.valueName = valueName;
    }

    @Override
    public String getMessage() {
        return String.format("%s [valueName: %s]", super.getMessage(), valueName);
    }
}
