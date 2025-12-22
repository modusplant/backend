package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidMemberProfileImagePathException extends BusinessException {
    public InvalidMemberProfileImagePathException() {
        super(MemberErrorCode.INVALID_MEMBER_PROFILE_IMAGE_PATH);
    }
}
