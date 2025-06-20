package kr.modusplant.domains.member.error;

import kr.modusplant.global.error.EntityExistsDomainException;

public class SiteMemberRoleExistsException extends EntityExistsDomainException {
    public SiteMemberRoleExistsException() {
        super("MEMBER_ROLE_EXISTS", "회원에 대한 역할 정보가 이미 있습니다.");
    }
}
