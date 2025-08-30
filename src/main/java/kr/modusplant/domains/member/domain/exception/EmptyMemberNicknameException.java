package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyMemberNicknameException extends BusinessException {
    public EmptyMemberNicknameException() {
        super(MemberErrorCode.EMPTY_MEMBER_NICKNAME);
    }
}
