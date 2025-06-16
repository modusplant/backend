package kr.modusplant.domains.member.error;

import kr.modusplant.global.error.EntityExistsDomainException;

public class SiteMemberAuthNotFoundException extends EntityExistsDomainException {
    public SiteMemberAuthNotFoundException() {
        super("MEMBER_AUTH_NOT_FOUND", "회원에 대한 인증 정보를 찾을 수 없습니다.");
    }
}
