package kr.modusplant.infrastructure.jwt.exception;

import kr.modusplant.infrastructure.jwt.exception.enums.AuthTokenErrorCode;

public class TokenKeyCreationException extends AuthTokenException {
    public TokenKeyCreationException() {
        super(AuthTokenErrorCode.INTERNAL_AUTHENTICATION_FAIL);
    }
}
