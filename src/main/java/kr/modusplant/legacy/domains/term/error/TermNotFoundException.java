package kr.modusplant.legacy.domains.term.error;

import kr.modusplant.global.vo.EntityName;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;

public class TermNotFoundException extends EntityNotFoundException {
    public TermNotFoundException() {
        super(ErrorCode.TERM_NOT_FOUND, EntityName.TERM);
    }
}
