package kr.modusplant.domains.term.error;

import kr.modusplant.global.error.EntityNotFoundDomainException;

public class TermNotFoundException extends EntityNotFoundDomainException {
    public TermNotFoundException() {
        super("TERM_NOT_FOUND", "약관을 찾을 수 없습니다.");
    }
}
