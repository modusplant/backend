package kr.modusplant.domains.identity.framework.legacy.error;

import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import lombok.Getter;

@Getter
public class BadCredentialException extends BusinessAuthenticationException {

    public BadCredentialException(IdentityErrorCode errorCode) {
        super(errorCode);
    }

    public BadCredentialException(IdentityErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
