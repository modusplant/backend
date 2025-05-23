package kr.modusplant.global.error;

import static kr.modusplant.global.enums.ExceptionMessage.INVALID_INPUT;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;

public class InvalidInputException extends IllegalArgumentException {

    public InvalidInputException(String fieldName, Object invalidValue, Class<?> clazz) {
        super(getFormattedExceptionMessage(INVALID_INPUT.getValue(), fieldName, invalidValue, clazz));
    }

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputException(Throwable cause) {
        super(cause);
    }

}
