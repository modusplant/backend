package kr.modusplant.domains.term.domain.exception;

import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyTermVersionException extends BusinessException {
    public EmptyTermVersionException() { super(TermErrorCode.EMPTY_TERM_VERSION); }
}
