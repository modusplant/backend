package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyNicknameException extends BusinessException {
    public EmptyNicknameException() {
        super(MemberErrorCode.EMPTY_NICKNAME);
    }
}
