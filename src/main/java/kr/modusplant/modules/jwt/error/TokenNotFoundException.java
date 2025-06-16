package kr.modusplant.modules.jwt.error;

import kr.modusplant.modules.jwt.app.error.AuthTokenException;
import org.springframework.http.HttpStatus;

public class TokenNotFoundException extends AuthTokenException {
    public TokenNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
