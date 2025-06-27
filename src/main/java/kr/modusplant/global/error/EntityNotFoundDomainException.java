package kr.modusplant.global.error;

import org.springframework.http.HttpStatus;

/**
 * {@code EntityNotFoundDomainException}은 {@link jakarta.persistence.EntityNotFoundException
 * EntityNotFoundException}과 같은 맥락에서 사용되지만, 도메인 기능을 활용하기 위해 도입되었습니다.
 */
public class EntityNotFoundDomainException extends DomainException {
    public EntityNotFoundDomainException(String code, String message) {
        super(HttpStatus.NOT_FOUND, code, message);
    }
}
