package kr.modusplant.domains.term.domain.exception;

import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyTermContentException extends BusinessException {
    public EmptyTermContentException() { super(TermErrorCode.EMPTY_TERM_CONTENT); }
}
