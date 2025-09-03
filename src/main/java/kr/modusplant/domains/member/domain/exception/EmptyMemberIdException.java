package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyMemberIdException extends BusinessException {
    public EmptyMemberIdException() {
        super(MemberErrorCode.EMPTY_MEMBER_ID);
    }
}
