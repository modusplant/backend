package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class PostAlreadyLikedException extends BusinessException {
    public PostAlreadyLikedException() {
        super(MemberErrorCode.POST_ALREADY_LIKED);
    }
}
