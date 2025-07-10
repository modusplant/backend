package kr.modusplant.global.middleware.security.error;

import kr.modusplant.global.middleware.security.enums.SecurityErrorCode;
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
