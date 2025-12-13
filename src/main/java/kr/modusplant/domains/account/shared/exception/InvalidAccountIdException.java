package kr.modusplant.domains.account.shared.exception;

import kr.modusplant.domains.account.shared.exception.enums.AccountErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidAccountIdException extends BusinessException {
    public InvalidAccountIdException() {
        super(AccountErrorCode.INVALID_ACCOUNT_ID);
    }
}
