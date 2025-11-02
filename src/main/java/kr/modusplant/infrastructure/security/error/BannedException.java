package kr.modusplant.infrastructure.security.error;

import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import lombok.Getter;

@Getter
public class BannedException extends BusinessAuthenticationException {

    public BannedException() {
        super(SecurityErrorCode.BANNED);
    }

    public BannedException(String message) {
        super(SecurityErrorCode.BANNED, message);
    }
}
