package kr.modusplant.global.middleware.security.error;

import org.springframework.security.core.AuthenticationException;

public class InactiveException extends AuthenticationException {

    public InactiveException(String msg, Throwable cause) { super(msg, cause); }

    public InactiveException(String msg) { super(msg); }
}
