package kr.modusplant.domains.member.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.EntityNotFoundException;
import kr.modusplant.global.vo.EntityName;

public class MemberNotFoundException extends EntityNotFoundException {

    private MemberNotFoundException(ErrorCode errorCode, String entityName) {
        super(errorCode, entityName);
    }

    public static MemberNotFoundException ofMember() {
        return new MemberNotFoundException(ErrorCode.SITEMEMBER_NOT_FOUND, EntityName.SITE_MEMBER);
    }

    public static MemberNotFoundException ofMemberAuth() {
        return new MemberNotFoundException(ErrorCode.SITEMEMBER_AUTH_NOT_FOUND, EntityName.SITE_MEMBER_AUTH);
    }

    public static MemberNotFoundException ofMemberRole() {
        return new MemberNotFoundException(ErrorCode.SITEMEMBER_ROLE_NOT_FOUND, EntityName.SITE_MEMBER_ROLE);
    }

    public static MemberNotFoundException ofMemberTerm() {
        return new MemberNotFoundException(ErrorCode.SITEMEMBER_TERM_NOT_FOUND, EntityName.SITE_MEMBER_TERM);
    }
}
