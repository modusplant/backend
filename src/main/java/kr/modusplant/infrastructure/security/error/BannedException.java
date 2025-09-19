package kr.modusplant.infrastructure.security.error;

import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import lombok.Getter;

@Getter
public class BannedException extends BusinessAuthenticationException {

    public BannedException() {
        super(IdentityErrorCode.BANNED);
    }

    public BannedException(String message) {
        super(IdentityErrorCode.BANNED, message);
    }
}
