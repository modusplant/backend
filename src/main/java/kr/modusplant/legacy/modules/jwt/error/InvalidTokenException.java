package kr.modusplant.legacy.modules.jwt.error;

import kr.modusplant.shared.exception.enums.ErrorCode;

public class InvalidTokenException extends AuthTokenException {
    public InvalidTokenException() {
        super(ErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
