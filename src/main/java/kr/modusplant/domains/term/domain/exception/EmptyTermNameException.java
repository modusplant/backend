package kr.modusplant.domains.term.domain.exception;

import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyTermNameException extends BusinessException {
    public EmptyTermNameException() { super(TermErrorCode.EMPTY_TERM_NAME); }
}
