package kr.modusplant.global.middleware.security.error;

import org.springframework.security.core.AuthenticationException;

public class InavtiveException extends AuthenticationException {

    public InavtiveException(String msg, Throwable cause) { super(msg, cause); }

    public InavtiveException(String msg) { super(msg); }
}
