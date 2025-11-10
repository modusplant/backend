package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class NotAccessiblePostLikeException extends BusinessException {
    public NotAccessiblePostLikeException() {
        super(MemberErrorCode.NOT_ACCESSIBLE_POST_LIKE);
    }
}
