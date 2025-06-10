package kr.modusplant.modules.jwt.error;

import kr.modusplant.modules.jwt.app.error.AuthTokenException;

public class TokenKeyCreationException extends AuthTokenException {
    public TokenKeyCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
