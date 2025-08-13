package kr.modusplant.legacy.modules.auth.social.error;

import kr.modusplant.domain.exception.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;
import lombok.Getter;

@Getter
public class OAuthRequestFailException extends BusinessException {

    private final String provider;

    public OAuthRequestFailException(ErrorCode errorCode, String provider) {
        super(errorCode);
        this.provider = provider;
    }
}
