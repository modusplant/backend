package kr.modusplant.infrastructure.jwt.exception;

import kr.modusplant.shared.exception.enums.ErrorCode;

public class TokenKeyStorageException extends AuthTokenException {
    public TokenKeyStorageException() {
        super(ErrorCode.INTERNAL_AUTHENTICATION_FAIL);
    }
}
