package kr.modusplant.legacy.modules.jwt.error;

import kr.modusplant.shared.exception.enums.ErrorCode;

public class TokenExpiredException extends AuthTokenException {
    public TokenExpiredException() {
        super(ErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
