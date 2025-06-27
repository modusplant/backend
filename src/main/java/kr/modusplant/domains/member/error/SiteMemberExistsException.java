package kr.modusplant.domains.member.error;

import kr.modusplant.global.error.EntityExistsDomainException;

public class SiteMemberExistsException extends EntityExistsDomainException {
    public SiteMemberExistsException() {
        super("MEMBER_EXISTS", "회원이 이미 있습니다.");
    }
}
