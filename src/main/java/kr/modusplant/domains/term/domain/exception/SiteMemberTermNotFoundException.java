package kr.modusplant.domains.term.domain.exception;

import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class SiteMemberTermNotFoundException extends BusinessException {
    public SiteMemberTermNotFoundException() { super(TermErrorCode.SITE_MEMBER_TERM_NOT_FOUND); }
}
