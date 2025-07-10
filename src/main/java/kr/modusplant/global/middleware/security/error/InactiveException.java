package kr.modusplant.global.middleware.security.error;

import kr.modusplant.global.middleware.security.enums.SecurityErrorCode;
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
