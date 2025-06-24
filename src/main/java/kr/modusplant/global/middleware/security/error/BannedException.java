package kr.modusplant.global.middleware.security.error;

import org.springframework.security.core.AuthenticationException;

public class BannedException extends AuthenticationException {
    public BannedException(String msg, Throwable cause) { super(msg, cause); }

    public BannedException(String msg) { super(msg); }
}
