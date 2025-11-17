package kr.modusplant.domains.term.domain.exception;

import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyTermIdException extends BusinessException {
    public EmptyTermIdException() { super(TermErrorCode.EMPTY_TERM_ID); }
}
