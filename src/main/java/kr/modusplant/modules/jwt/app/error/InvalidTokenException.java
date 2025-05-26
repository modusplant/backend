package kr.modusplant.modules.jwt.app.error;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AuthTokenException {
    public InvalidTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
