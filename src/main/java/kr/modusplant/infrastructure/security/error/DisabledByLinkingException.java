package kr.modusplant.infrastructure.security.error;

import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import lombok.Getter;

@Getter
public class DisabledByLinkingException extends BusinessAuthenticationException {

    public DisabledByLinkingException() {
        super(IdentityErrorCode.DISABLED_BY_LINKING);
    }

    public DisabledByLinkingException(String message) {
        super(IdentityErrorCode.DISABLED_BY_LINKING, message);
    }
}
