package kr.modusplant.global.middleware.security.error;

import kr.modusplant.global.middleware.security.enums.SecurityErrorCode;
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
