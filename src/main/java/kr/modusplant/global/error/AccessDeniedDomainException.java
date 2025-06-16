package kr.modusplant.global.error;

import org.springframework.http.HttpStatus;

public class AccessDeniedDomainException extends DomainException {
    public AccessDeniedDomainException(String code, String message) {
        super(HttpStatus.UNAUTHORIZED, code, message);
    }
}
