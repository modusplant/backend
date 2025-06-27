package kr.modusplant.domains.member.error;

import kr.modusplant.global.error.EntityExistsDomainException;

public class SiteMemberAuthExistsException extends EntityExistsDomainException {
    public SiteMemberAuthExistsException() {
        super("MEMBER_AUTH_EXISTS", "회원에 대한 인증 정보가 이미 있습니다.");
    }
}
