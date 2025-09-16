package kr.modusplant.domains.security.framework.legacy.error;

import kr.modusplant.domains.security.domain.exception.enums.SecurityErrorCode;
import lombok.Getter;

@Getter
public class BadCredentialException extends BusinessAuthenticationException {

    public BadCredentialException(SecurityErrorCode errorCode) {
        super(errorCode);
    }

    public BadCredentialException(SecurityErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
