package kr.modusplant.domains.security.framework.legacy.error;

import kr.modusplant.domains.security.domain.exception.enums.SecurityErrorCode;
import lombok.Getter;

@Getter
public class DisabledByLinkingException extends BusinessAuthenticationException {

    public DisabledByLinkingException() {
        super(SecurityErrorCode.DISABLED_BY_LINKING);
    }

    public DisabledByLinkingException(String message) {
        super(SecurityErrorCode.DISABLED_BY_LINKING, message);
    }
}
