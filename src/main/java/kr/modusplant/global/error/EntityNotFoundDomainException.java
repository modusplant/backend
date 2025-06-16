package kr.modusplant.global.error;

import org.springframework.http.HttpStatus;

public class EntityNotFoundDomainException extends DomainException {
    public EntityNotFoundDomainException(String code, String message) {
        super(HttpStatus.NOT_FOUND, code, message);
    }
}
