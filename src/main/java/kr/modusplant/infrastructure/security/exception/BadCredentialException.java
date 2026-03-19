package kr.modusplant.infrastructure.security.exception;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;

@Getter
public class BadCredentialException extends BusinessAuthenticationException {

    public BadCredentialException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BadCredentialException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
