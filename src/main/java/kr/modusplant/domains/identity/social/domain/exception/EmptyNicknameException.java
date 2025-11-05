package kr.modusplant.domains.identity.social.domain.exception;

import kr.modusplant.domains.identity.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyNicknameException extends BusinessException {
    public EmptyNicknameException() {
        super(SocialIdentityErrorCode.EMPTY_NICKNAME);
    }
}
