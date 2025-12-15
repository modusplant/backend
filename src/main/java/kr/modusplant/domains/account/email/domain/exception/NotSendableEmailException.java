package kr.modusplant.domains.account.email.domain.exception;

import kr.modusplant.domains.account.email.domain.exception.enums.EmailIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class NotSendableEmailException extends BusinessException {
    public NotSendableEmailException() {
        super(EmailIdentityErrorCode.NOT_SENDABLE_EMAIL);
    }
}
