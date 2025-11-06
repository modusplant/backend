package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyMemberProfileImagePathException extends BusinessException {
    public EmptyMemberProfileImagePathException() {
        super(MemberErrorCode.EMPTY_MEMBER_PROFILE_IMAGE_PATH);
    }
}
