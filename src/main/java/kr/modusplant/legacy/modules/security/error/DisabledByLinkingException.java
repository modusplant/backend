package kr.modusplant.legacy.modules.security.error;

import kr.modusplant.legacy.modules.security.enums.SecurityErrorCode;
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
