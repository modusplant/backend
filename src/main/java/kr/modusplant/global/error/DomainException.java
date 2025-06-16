package kr.modusplant.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DomainException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
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
