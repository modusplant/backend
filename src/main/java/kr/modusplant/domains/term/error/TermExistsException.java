package kr.modusplant.domains.term.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.EntityExistsException;
import kr.modusplant.global.vo.EntityName;

public class TermExistsException extends EntityExistsException {

    public TermExistsException() {
        super(ErrorCode.TERM_EXISTS, EntityName.TERM);
    }
}
