package kr.modusplant.infrastructure.jwt.exception;

import kr.modusplant.infrastructure.jwt.exception.enums.AuthTokenErrorCode;

public class InvalidTokenException extends AuthTokenException {
    public InvalidTokenException() {
        super(AuthTokenErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
