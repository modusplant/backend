package kr.modusplant.legacy.modules.jwt.error;

import kr.modusplant.global.error.BusinessException;
import kr.modusplant.infrastructure.exception.enums.ErrorCode;

public class TokenNotFoundException extends BusinessException {
    public TokenNotFoundException() {
        super(ErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
