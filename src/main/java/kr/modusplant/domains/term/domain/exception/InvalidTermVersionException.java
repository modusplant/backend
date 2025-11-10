package kr.modusplant.domains.term.domain.exception;

import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidTermVersionException extends BusinessException {
    public InvalidTermVersionException() { super(TermErrorCode.INVALID_TERM_VERSION); }
}
