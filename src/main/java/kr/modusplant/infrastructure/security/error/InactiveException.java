package kr.modusplant.infrastructure.security.error;

import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import lombok.Getter;

@Getter
public class InactiveException extends BusinessAuthenticationException {

    public InactiveException() {
        super(IdentityErrorCode.INACTIVE);
    }

    public InactiveException(String message) {
        super(IdentityErrorCode.INACTIVE, message);
    }
}
