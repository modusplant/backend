package kr.modusplant.modules.jwt.error;

import org.springframework.http.HttpStatus;

public class TokenDataNotFoundException extends AuthTokenException {
    public TokenDataNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND,message);
    }
}
