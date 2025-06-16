package kr.modusplant.domains.member.error;

import kr.modusplant.global.error.EntityNotFoundDomainException;

public class SiteMemberRoleNotFoundException extends EntityNotFoundDomainException {
    public SiteMemberRoleNotFoundException() {
        super("MEMBER_ROLE_NOT_FOUND", "회원에 대한 역할 정보를 찾을 수 없습니다.");
    }
}
