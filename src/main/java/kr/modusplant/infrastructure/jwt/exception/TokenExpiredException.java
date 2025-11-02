package kr.modusplant.infrastructure.jwt.exception;

import kr.modusplant.shared.exception.enums.ErrorCode;

public class TokenExpiredException extends AuthTokenException {
    public TokenExpiredException() {
        super(ErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
