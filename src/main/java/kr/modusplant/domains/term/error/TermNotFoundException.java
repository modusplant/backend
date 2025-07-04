package kr.modusplant.domains.term.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.EntityNotFoundException;
import kr.modusplant.global.vo.EntityName;

public class TermNotFoundException extends EntityNotFoundException {
    public TermNotFoundException() {
        super(ErrorCode.TERM_NOT_FOUND, EntityName.TERM);
    }
}
