package kr.modusplant.global.error;

import kr.modusplant.global.advice.GlobalExceptionHandler;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * {@code DomainException}은 최상위 인터페이스로서의 도메인 예외입니다. 해당 예외의 필드는
 * {@link kr.modusplant.global.app.http.response.DataResponse DataResponse} 와 매칭됩니다.
 * 해당 예외는 {@link GlobalExceptionHandler GlobalExceptionHandler}에서 처리되며,
 * 여기서 해당 예외 및 하위 예외는 모두 동일하게 처리됩니다.
 */
@Getter
public class DomainException extends RuntimeException {
    private final HttpStatus status;

    /**
     * 각 예외를 대표하는 코드로, 상수 서식(밑줄로 구분된 대문자)을 따릅니다.
     */
    private final String code;

    /**
     * 각 예외를 서술하는 메시지로, 코드와 동일하게 하나의 예외는 하나의 메시지만을 갖습니다. 한글로 구성합니다.
     */
    private final String message;

    public DomainException(String code, String message) {
        this.status = HttpStatus.BAD_REQUEST;
        this.code = code;
        this.message = message;
    }

    public DomainException(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public DomainException(HttpStatus status, String code, String message, Throwable cause) {
        super(cause);
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
