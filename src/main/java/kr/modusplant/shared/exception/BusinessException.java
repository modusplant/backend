package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;

import java.time.Instant;

/**
 * {@code BusinessException}은 모든 커스텀 예외의 상위 클래스입니다.
 *
 * <p>커스텀 예외를 {@link kr.modusplant.shared.exception.supers.ErrorCode}와 대응시키는 역할을 합니다.
 * 모든 커스텀 예외는 반드시 {@code BusinessException}을 상속받거나
 * 해당 클래스의 서브 클래스를 상속받아야 합니다.</p>
 *
 * @author Jun Hee
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Instant causedAt;

    public BusinessException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
        this.causedAt = Instant.now();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.causedAt = Instant.now();
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.causedAt = Instant.now();
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.causedAt = Instant.now();
    }

    @Override
    public String getMessage() {
        return String.format("%s [errorCode: %s, causedAt: %s]", super.getMessage(), errorCode, causedAt);
    }
}
