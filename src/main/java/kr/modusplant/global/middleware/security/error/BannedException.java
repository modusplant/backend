package kr.modusplant.global.middleware.security.error;

import kr.modusplant.global.middleware.security.enums.SecurityErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class BannedException extends AuthenticationException {

    private final SecurityErrorCode errorCode;

    public BannedException() {
        super(SecurityErrorCode.BANNED.getMessage());
        this.errorCode = SecurityErrorCode.BANNED;
    }

    public BannedException(String message) {
        super(message);
        this.errorCode = SecurityErrorCode.BANNED;
    }
}
