package kr.modusplant.global.middleware.security.error;

import org.springframework.security.core.AuthenticationException;

public class DisabledByLinkingException extends AuthenticationException {
    public DisabledByLinkingException(String msg, Throwable cause) { super(msg, cause); }

    public DisabledByLinkingException(String msg) { super(msg); }
}
