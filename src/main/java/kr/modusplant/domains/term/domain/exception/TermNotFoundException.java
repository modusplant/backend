package kr.modusplant.domains.term.domain.exception;

import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class TermNotFoundException extends BusinessException {
    public TermNotFoundException() { super(TermErrorCode.TERM_NOT_FOUND); }
}
