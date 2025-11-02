package kr.modusplant.infrastructure.jwt.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;

public class TokenNotFoundException extends BusinessException {
    public TokenNotFoundException() {
        super(ErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
