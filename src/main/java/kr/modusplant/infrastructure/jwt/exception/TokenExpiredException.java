package kr.modusplant.infrastructure.jwt.exception;

import kr.modusplant.infrastructure.jwt.exception.enums.AuthTokenErrorCode;

public class TokenExpiredException extends AuthTokenException {
    public TokenExpiredException() {
        super(AuthTokenErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
