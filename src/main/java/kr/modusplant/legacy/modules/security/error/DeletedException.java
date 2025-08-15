package kr.modusplant.legacy.modules.security.error;

import kr.modusplant.legacy.modules.security.enums.SecurityErrorCode;
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
