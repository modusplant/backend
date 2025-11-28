package kr.modusplant.infrastructure.security.exception;

import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
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
