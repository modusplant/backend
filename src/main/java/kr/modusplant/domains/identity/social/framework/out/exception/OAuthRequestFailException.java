package kr.modusplant.domains.identity.social.framework.out.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class OAuthRequestFailException extends BusinessException {

    private final String provider;

    public OAuthRequestFailException(ErrorCode errorCode, String provider) {
        super(errorCode);
        this.provider = provider;
    }
}
