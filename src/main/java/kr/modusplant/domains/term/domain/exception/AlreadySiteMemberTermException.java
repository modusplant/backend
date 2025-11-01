package kr.modusplant.domains.term.domain.exception;

import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class AlreadySiteMemberTermException extends BusinessException {
    public AlreadySiteMemberTermException() {
        super(TermErrorCode.ALREADY_SITE_MEMBER_TERM);
    }
}
