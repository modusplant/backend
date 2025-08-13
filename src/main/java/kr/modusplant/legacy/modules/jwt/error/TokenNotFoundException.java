package kr.modusplant.legacy.modules.jwt.error;

import kr.modusplant.domain.exception.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;

public class TokenNotFoundException extends BusinessException {
    public TokenNotFoundException() {
        super(ErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
