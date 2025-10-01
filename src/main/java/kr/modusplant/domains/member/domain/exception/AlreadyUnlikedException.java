package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class AlreadyUnlikedException extends BusinessException {
    public AlreadyUnlikedException() {
        super(MemberErrorCode.ALREADY_UNLIKED);
    }
}
