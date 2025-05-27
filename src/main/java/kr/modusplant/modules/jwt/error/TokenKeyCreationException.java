package kr.modusplant.modules.jwt.error;

public class TokenKeyCreationException extends AuthTokenException {
    public TokenKeyCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
