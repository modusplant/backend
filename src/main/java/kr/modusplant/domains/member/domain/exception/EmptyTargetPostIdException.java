package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyTargetPostIdException extends BusinessException {
    public EmptyTargetPostIdException() {
        super(MemberErrorCode.EMPTY_TARGET_POST_ID);
    }
}
