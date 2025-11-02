package kr.modusplant.infrastructure.jwt.exception;

import kr.modusplant.shared.exception.enums.ErrorCode;

public class TokenKeyCreationException extends AuthTokenException {
    public TokenKeyCreationException() {
        super(ErrorCode.INTERNAL_AUTHENTICATION_FAIL);
    }
}
