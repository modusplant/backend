package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyMemberProfileImageBytesException extends BusinessException {
    public EmptyMemberProfileImageBytesException() {
        super(MemberErrorCode.EMPTY_MEMBER_PROFILE_IMAGE_BYTES);
    }
}
