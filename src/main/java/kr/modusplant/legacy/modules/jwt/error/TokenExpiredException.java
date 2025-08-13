package kr.modusplant.legacy.modules.jwt.error;

import kr.modusplant.domain.exception.enums.ErrorCode;

public class TokenExpiredException extends AuthTokenException {
    public TokenExpiredException() {
        super(ErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
