package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.ErrorCode;
import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import lombok.Getter;

import java.time.Instant;

/**
 * {@code BusinessException}은 모든 커스텀 예외의 상위 클래스입니다.
 *
 * <p>커스텀 예외를 {@link ErrorCode}와 대응시키는 역할을 합니다.
 * 모든 커스텀 예외는 반드시 {@code BusinessException}을 상속받거나
 * 해당 클래스의 서브 클래스를 상속받아야 합니다.</p>
 *
 * @author Jun Hee
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ResponseCode errorCode;
    private final Instant causedAt;

    public BusinessException(ResponseCode errorCode) {
        super();
        this.errorCode = errorCode;
        this.causedAt = Instant.now();
    }

    public BusinessException(ResponseCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.causedAt = Instant.now();
    }

    public BusinessException(ResponseCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.causedAt = Instant.now();
    }

    public BusinessException(ResponseCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.causedAt = Instant.now();
    }
}
