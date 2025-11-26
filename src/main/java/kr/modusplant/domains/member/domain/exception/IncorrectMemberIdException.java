package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class IncorrectMemberIdException extends BusinessException {
    public IncorrectMemberIdException() {
        super(MemberErrorCode.INCORRECT_MEMBER_ID);
    }
}
