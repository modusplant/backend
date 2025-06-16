package kr.modusplant.global.error;

public class EntityExistsDomainException extends DomainException {
    public EntityExistsDomainException(String code, String message) {
        super(code, message);
    }
}
