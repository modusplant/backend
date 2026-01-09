package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.supers.ErrorCode;

/**
 * 클라이언트가 조회하려는 데이터가 존재하지 않는 경우에 발생하는 예외입니다.
 * 사용자가 이메일 정보를 요청했는데 찾을 수 없는 경우가 해당됩니다.
 * 찾으려는 DB 컬럼 자체가 없는 경우는 {@link kr.modusplant.framework.jpa.exception.NotFoundEntityException}이 발생합니다.
 */
public class NotFoundValueException extends BusinessException {

    private final String valueName;

    public NotFoundValueException(ErrorCode errorCode, String valueName) {
        super(errorCode);
        this.valueName = valueName;
    }

    public NotFoundValueException(ErrorCode errorCode, String valueName, String message) {
        super(errorCode, message);
        this.valueName = valueName;

    }

    public NotFoundValueException(ErrorCode errorCode, String valueName, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.valueName = valueName;
    }

    public NotFoundValueException(ErrorCode errorCode, String valueName, Throwable cause) {
        super(errorCode, cause);
        this.valueName = valueName;
    }

    @Override
    public String getMessage() {
        return String.format("%s [valueName: %s]", super.getMessage(), valueName);
    }
}
