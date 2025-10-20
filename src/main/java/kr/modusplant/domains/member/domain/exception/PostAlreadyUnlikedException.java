package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class PostAlreadyUnlikedException extends BusinessException {
    public PostAlreadyUnlikedException() {
        super(MemberErrorCode.POST_ALREADY_UNLIKED);
    }
}
