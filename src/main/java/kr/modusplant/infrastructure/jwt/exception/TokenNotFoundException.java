package kr.modusplant.infrastructure.jwt.exception;

import kr.modusplant.infrastructure.jwt.exception.enums.AuthTokenErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class TokenNotFoundException extends BusinessException {
    public TokenNotFoundException() {
        super(AuthTokenErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
