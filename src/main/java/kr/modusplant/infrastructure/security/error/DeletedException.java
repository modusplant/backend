package kr.modusplant.infrastructure.security.error;

import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import lombok.Getter;

@Getter
public class DeletedException extends BusinessAuthenticationException {

    public DeletedException() {
        super(IdentityErrorCode.DELETED);
    }

    public DeletedException(String message) {
        super(IdentityErrorCode.DELETED, message);
    }
}
