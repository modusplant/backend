package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;

@Getter
public class DataLengthException extends InvalidValueException {
    public DataLengthException(ErrorCode errorCode, String valueName) {
        super(errorCode, valueName);
    }

    public DataLengthException(ErrorCode errorCode, String valueName, String message) {
        super(errorCode, valueName, message);
    }

    public DataLengthException(ErrorCode errorCode, String valueName, String message, Throwable cause) {
        super(errorCode, valueName, message, cause);
    }

    public DataLengthException(ErrorCode errorCode, String valueName, Throwable cause) {
        super(errorCode, valueName, cause);
    }

}