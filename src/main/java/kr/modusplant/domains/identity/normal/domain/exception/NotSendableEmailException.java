package kr.modusplant.domains.identity.normal.domain.exception;

import kr.modusplant.domains.identity.normal.domain.exception.enums.NormalIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class NotSendableEmailException extends BusinessException {
    public NotSendableEmailException() {
        super(NormalIdentityErrorCode.NOT_SENDABLE_EMAIL);
    }
}
