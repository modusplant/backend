package kr.modusplant.legacy.domains.term.error;

import kr.modusplant.global.vo.EntityName;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.enums.ErrorCode;

public class TermExistsException extends EntityExistsException {

    public TermExistsException() {
        super(ErrorCode.TERM_EXISTS, EntityName.TERM);
    }
}
