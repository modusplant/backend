package kr.modusplant.modules.jwt.error;

import org.springframework.http.HttpStatus;

public class TokenNotFoundException extends AuthTokenException {
    public TokenNotFoundException() {
        super(HttpStatus.NOT_FOUND, "TOKEN_NOT_FOUND", "토큰을 찾을 수 없습니다.");
    }
}
