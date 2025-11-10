package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyMemberProfileIntroductionException extends BusinessException {
    public EmptyMemberProfileIntroductionException() {
        super(MemberErrorCode.EMPTY_MEMBER_PROFILE_INTRODUCTION);
    }
}
