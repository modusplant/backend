package kr.modusplant.infrastructure.config.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;

/**
 * 구성이 올바르게 이뤄지지 않는 경우에 발생하는 예외입니다.
 * 세마포어에서 허용하는 커넥션의 수가 HikariCP 커넥션 풀보다 많은 경우가 해당됩니다.
 */
@Getter
public class ConfigurationException extends BusinessException {

    private final String[] valueNames;

    public ConfigurationException(ErrorCode errorCode, String[] valueNames) {
        super(errorCode);
        this.valueNames = valueNames;
    }

    public ConfigurationException(ErrorCode errorCode, String[] valueNames, String message) {
        super(errorCode, message);
        this.valueNames = valueNames;
    }

    public ConfigurationException(ErrorCode errorCode, String[] valueNames, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.valueNames = valueNames;
    }

    public ConfigurationException(ErrorCode errorCode, String[] valueNames, Throwable cause) {
        super(errorCode, cause);
        this.valueNames = valueNames;
    }

    @Override
    public String getMessage() {
        return String.format("%s [valueNames: %s]", super.getMessage(), String.join(", ", valueNames));
    }
}
