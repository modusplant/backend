package kr.modusplant.legacy.modules.security.error;

import kr.modusplant.legacy.modules.security.enums.SecurityErrorCode;
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
