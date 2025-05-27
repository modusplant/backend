package kr.modusplant.modules.jwt.error;

import org.springframework.http.HttpStatus;

public class TokenNotFoundException extends AuthTokenException {
    public TokenNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
