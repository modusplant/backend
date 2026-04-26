package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;

import java.util.List;

/**
 * 클라이언트 요청 필드의 값이 해당 값의 형식과 맞지 않는 경우에 발생하는 예외입니다.
 * 이메일, 비밀번호, 닉네임 등의 값이 각자의 정규 표현식과 맞지 않는 경우가 해당됩니다.
 */
@Getter
public class InvalidValueException extends BusinessException {

    private final List<String> valueNames;

    public InvalidValueException(ErrorCode errorCode, String valueName) {
        super(errorCode);
        this.valueNames = List.of(valueName);
    }

    public InvalidValueException(ErrorCode errorCode, List<String> valueNames) {
        super(errorCode);
        this.valueNames = valueNames;
    }

    public InvalidValueException(ErrorCode errorCode, List<String> valueNames, String message) {
        super(errorCode, message);
        this.valueNames = valueNames;
    }

    public InvalidValueException(ErrorCode errorCode, List<String> valueNames, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.valueNames = valueNames;
    }

    public InvalidValueException(ErrorCode errorCode, List<String> valueNames, Throwable cause) {
        super(errorCode, cause);
        this.valueNames = valueNames;
    }

    @Override
    public String getMessage() {
        return String.format("%s [valueNames: %s]", super.getMessage(), valueNames);
    }
}
