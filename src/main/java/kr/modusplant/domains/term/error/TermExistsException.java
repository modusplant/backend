package kr.modusplant.domains.term.error;

import kr.modusplant.global.error.EntityExistsDomainException;

public class TermExistsException extends EntityExistsDomainException {
    public TermExistsException() {
        super("TERM_EXISTS", "약관이 이미 있습니다.");
    }
}
