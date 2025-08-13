package kr.modusplant.legacy.modules.jwt.error;

import kr.modusplant.shared.exception.enums.ErrorCode;

public class TokenKeyCreationException extends AuthTokenException {
    public TokenKeyCreationException() {
        super(ErrorCode.INTERNAL_AUTHENTICATION_FAIL);
    }
}
