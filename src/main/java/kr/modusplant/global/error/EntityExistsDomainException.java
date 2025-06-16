package kr.modusplant.global.error;

/**
 * {@code EntityExistsDomainException}은 {@link jakarta.persistence.EntityExistsException
 * EntityExistsException}과 같은 맥락에서 사용되지만, 도메인 기능을 활용하기 위해 도입되었습니다.
 */
public class EntityExistsDomainException extends DomainException {
    public EntityExistsDomainException(String code, String message) {
        super(code, message);
    }
}
