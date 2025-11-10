package kr.modusplant.domains.term.domain.exception;

import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class SiteMemberNotFoundException extends BusinessException {
    public SiteMemberNotFoundException() { super(TermErrorCode.SITE_MEMBER_NOT_FOUND); }
}
