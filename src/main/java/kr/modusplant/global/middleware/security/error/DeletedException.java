package kr.modusplant.global.middleware.security.error;

import kr.modusplant.global.middleware.security.enums.SecurityErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class DeletedException extends BusinessAuthenticationException {

    public DeletedException() {
        super(SecurityErrorCode.DELETED);
    }

    public DeletedException(String message) {
        super(SecurityErrorCode.DELETED, message);
    }
}
