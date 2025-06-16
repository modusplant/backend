package kr.modusplant.domains.member.error;

import kr.modusplant.global.error.EntityNotFoundDomainException;

public class SiteMemberTermNotFoundException extends EntityNotFoundDomainException {
    public SiteMemberTermNotFoundException() {
        super("MEMBER_TERM_NOT_FOUND", "회원에 대한 약관 정보를 찾을 수 없습니다.");
    }
}
