package kr.modusplant.domains.identity.social.domain.exception;

import kr.modusplant.domains.identity.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidNicknameException extends BusinessException {
    public InvalidNicknameException() {
        super(SocialIdentityErrorCode.INVALID_NICKNAME);
    }
}
