package kr.modusplant.modules.jwt.app.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthTokenException extends RuntimeException {
    private final HttpStatus status;

    protected AuthTokenException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
