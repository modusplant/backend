package kr.modusplant.domains.identity.email.domain.exception;

import kr.modusplant.domains.identity.email.domain.exception.enums.EmailErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class NotSendableEmailException extends BusinessException {
    public NotSendableEmailException() {
        super(EmailErrorCode.NOT_SENDABLE_EMAIL);
    }
}
