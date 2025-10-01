package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class AlreadyLikedException extends BusinessException {
    public AlreadyLikedException() {
        super(MemberErrorCode.ALREADY_LIKED);
    }
}
