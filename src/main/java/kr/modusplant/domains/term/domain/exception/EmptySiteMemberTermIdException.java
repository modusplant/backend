package kr.modusplant.domains.term.domain.exception;

import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptySiteMemberTermIdException extends BusinessException {
    public EmptySiteMemberTermIdException() { super(TermErrorCode.EMPTY_SITE_MEMBER_TERM_ID); }
}
