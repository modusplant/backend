package kr.modusplant.domains.member.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.EntityExistsException;
import kr.modusplant.global.vo.EntityName;

public class MemberExistsException extends EntityExistsException {

    private MemberExistsException(ErrorCode errorCode, String entityName) {
        super(errorCode, entityName);
    }

    public static MemberExistsException ofMember() {
        return new MemberExistsException(ErrorCode.SITEMEMBER_EXISTS, EntityName.SITE_MEMBER);
    }

    public static MemberExistsException ofMemberAuth() {
        return new MemberExistsException(ErrorCode.SITEMEMBER_AUTH_EXISTS, EntityName.SITE_MEMBER_AUTH);
    }

    public static MemberExistsException ofMemberRole() {
        return new MemberExistsException(ErrorCode.SITEMEMBER_ROLE_EXISTS, EntityName.SITE_MEMBER_ROLE);
    }

    public static MemberExistsException ofMemberTerm() {
        return new MemberExistsException(ErrorCode.SITEMEMBER_TERM_EXISTS, EntityName.SITE_MEMBER_TERM);
    }
}
