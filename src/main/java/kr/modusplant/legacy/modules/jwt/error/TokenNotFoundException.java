package kr.modusplant.legacy.modules.jwt.error;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;

public class TokenNotFoundException extends BusinessException {
    public TokenNotFoundException() {
        super(ErrorCode.CREDENTIAL_NOT_AUTHORIZED);
    }
}
