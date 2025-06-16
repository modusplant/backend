package kr.modusplant.domains.member.error;

import kr.modusplant.global.error.EntityExistsDomainException;

public class SiteMemberNotFoundException extends EntityExistsDomainException {
    public SiteMemberNotFoundException() {
        super("MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다.");
    }
}
