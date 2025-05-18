package kr.modusplant.modules.jwt.app.error;

public class TokenKeyCreationException extends AuthTokenException {
    public TokenKeyCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
