package kr.modusplant.modules.jwt.error;

import org.springframework.http.HttpStatus;

public class TokenKeyCreationException extends AuthTokenException {
    public TokenKeyCreationException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_CREATION_ERROR", "토큰 생성에 실패했습니다.");
    }
}
