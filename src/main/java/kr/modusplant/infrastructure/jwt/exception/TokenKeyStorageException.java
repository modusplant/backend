package kr.modusplant.infrastructure.jwt.exception;

import kr.modusplant.infrastructure.jwt.exception.enums.AuthTokenErrorCode;

public class TokenKeyStorageException extends AuthTokenException {
    public TokenKeyStorageException() {
        super(AuthTokenErrorCode.INTERNAL_AUTHENTICATION_FAIL);
    }
}
