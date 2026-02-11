package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.supers.ErrorCode;

/**
 * 클라이언트가 조회하려는 데이터가 이미 존재하는 경우에 발생하는 예외입니다.
 * 사용자가 변경하려는 닉네임이 존재하는 경우가 해당됩니다.
 * 찾으려는 DB 컬럼 자체가 이미 있는 경우는 {@link kr.modusplant.framework.jpa.exception.ExistsEntityException}이 발생합니다.
 */
public class ExistsValueException extends BusinessException {

    private final String valueName;

    public ExistsValueException(ErrorCode errorCode, String valueName) {
        super(errorCode);
        this.valueName = valueName;
    }

    public ExistsValueException(ErrorCode errorCode, String valueName, String message) {
        super(errorCode, message);
        this.valueName = valueName;

    }

    public ExistsValueException(ErrorCode errorCode, String valueName, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.valueName = valueName;
    }

    public ExistsValueException(ErrorCode errorCode, String valueName, Throwable cause) {
        super(errorCode, cause);
        this.valueName = valueName;
    }

    @Override
    public String getMessage() {
        return String.format("%s [valueName: %s]", super.getMessage(), valueName);
    }
}
