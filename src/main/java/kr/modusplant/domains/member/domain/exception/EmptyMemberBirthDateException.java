package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyMemberBirthDateException extends BusinessException {
    public EmptyMemberBirthDateException() {
        super(MemberErrorCode.EMPTY_MEMBER_BIRTH_DATE);
    }
}
