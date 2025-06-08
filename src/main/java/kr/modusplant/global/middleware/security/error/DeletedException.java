package kr.modusplant.global.middleware.security.error;

import org.springframework.security.core.AuthenticationException;

public class DeletedException extends AuthenticationException {
    public DeletedException(String msg, Throwable cause) { super(msg, cause); }

    public DeletedException(String msg) { super(msg); }
}
