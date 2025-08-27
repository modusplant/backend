package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyMemberStatusException extends BusinessException {
    public EmptyMemberStatusException() {
        super(MemberErrorCode.EMPTY_STATUS);
    }
}
