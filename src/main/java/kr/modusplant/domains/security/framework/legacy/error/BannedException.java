package kr.modusplant.domains.security.framework.legacy.error;

import kr.modusplant.domains.security.framework.legacy.enums.SecurityErrorCode;
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
