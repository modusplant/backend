package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyTargetCommentPathException extends BusinessException {
    public EmptyTargetCommentPathException() {
        super(MemberErrorCode.EMPTY_TARGET_COMMENT_PATH);
    }
}
