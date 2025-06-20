package kr.modusplant.global.error;

import org.springframework.http.HttpStatus;

/**
 * {@code AccessDeniedDomainException}은 {@link org.springframework.security.access.AccessDeniedException
 * AccessDeniedException}과 같은 맥락에서 사용되지만, 도메인 기능을 활용하기 위해 도입되었습니다.
 */
public class AccessDeniedDomainException extends DomainException {
    public AccessDeniedDomainException(String code, String message) {
        super(HttpStatus.UNAUTHORIZED, code, message);
    }
}
