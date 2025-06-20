package kr.modusplant.modules.jwt.app.error;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AuthTokenException {
    public InvalidTokenException() {
        super(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "토큰이 유효하지 않습니다.");
    }
}
