package kr.modusplant.domains.account.shared.exception;

import kr.modusplant.domains.account.shared.exception.enums.AccountErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyAccountIdException extends BusinessException {
    public EmptyAccountIdException() {
        super(AccountErrorCode.EMPTY_ACCOUNT_ID);
    }
}
