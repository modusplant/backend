package kr.modusplant.global.middleware.security.error;

import kr.modusplant.global.middleware.security.enums.SecurityErrorCode;
import lombok.Getter;

@Getter
public class DeletedException extends BusinessAuthenticationException {

    public DeletedException() {
        super(SecurityErrorCode.DELETED);
    }

    public DeletedException(String message) {
        super(SecurityErrorCode.DELETED, message);
    }
}
