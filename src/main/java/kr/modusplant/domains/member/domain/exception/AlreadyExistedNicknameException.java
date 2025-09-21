package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class AlreadyExistedNicknameException extends BusinessException {
    public AlreadyExistedNicknameException() {
        super(MemberErrorCode.ALREADY_EXISTED_NICKNAME);
    }
}
