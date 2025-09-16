package kr.modusplant.domains.security.framework.legacy.error;

import kr.modusplant.domains.security.domain.exception.enums.SecurityErrorCode;
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
