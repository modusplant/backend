package kr.modusplant.global.middleware.security.error;

import kr.modusplant.global.middleware.security.enums.SecurityErrorCode;
import lombok.Getter;
import org.springframework.security.authentication.BadCredentialsException;

@Getter
public class BadCredentialException extends BadCredentialsException {

    private final SecurityErrorCode errorCode;

    public BadCredentialException(SecurityErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BadCredentialException(SecurityErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
