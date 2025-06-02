package kr.modusplant.modules.jwt.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthTokenException extends RuntimeException {
    private HttpStatus status;

    protected AuthTokenException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public AuthTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
