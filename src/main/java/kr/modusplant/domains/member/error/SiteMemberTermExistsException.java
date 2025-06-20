package kr.modusplant.domains.member.error;

import kr.modusplant.global.error.EntityExistsDomainException;

public class SiteMemberTermExistsException extends EntityExistsDomainException {
    public SiteMemberTermExistsException() {
        super("MEMBER_TERM_EXISTS", "회원에 대한 약관 정보가 이미 있습니다.");
    }
}
