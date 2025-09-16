package kr.modusplant.domains.security.framework.legacy.error;

import kr.modusplant.domains.security.framework.legacy.enums.SecurityErrorCode;
import lombok.Getter;

@Getter
public class InactiveException extends BusinessAuthenticationException {

    public InactiveException() {
        super(SecurityErrorCode.INACTIVE);
    }

    public InactiveException(String message) {
        super(SecurityErrorCode.INACTIVE, message);
    }
}
