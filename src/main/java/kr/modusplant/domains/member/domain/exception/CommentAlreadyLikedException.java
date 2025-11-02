package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class CommentAlreadyLikedException extends BusinessException {
    public CommentAlreadyLikedException() {
        super(MemberErrorCode.COMMENT_ALREADY_LIKED);
    }
}
