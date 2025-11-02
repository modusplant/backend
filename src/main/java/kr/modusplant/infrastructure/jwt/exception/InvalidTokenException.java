package kr.modusplant.infrastructure.jwt.exception;

import kr.modusplant.shared.exception.enums.ErrorCode;

public class InvalidTokenException extends AuthTokenException {
    public InvalidTokenException() {
        super(ErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
