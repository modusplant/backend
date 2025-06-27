package kr.modusplant.domains.communication.common.error;

import kr.modusplant.global.error.EntityNotFoundDomainException;

public class CategoryNotFoundException extends EntityNotFoundDomainException {
    public CategoryNotFoundException() {
        super("CATEGORY_NOT_FOUND", "항목을 찾을 수 없습니다.");
    }
}