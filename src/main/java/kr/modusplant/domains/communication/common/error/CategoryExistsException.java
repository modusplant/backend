package kr.modusplant.domains.communication.common.error;

import kr.modusplant.global.error.EntityExistsDomainException;

public class CategoryExistsException extends EntityExistsDomainException {
    public CategoryExistsException() {
        super("CATEGORY_EXISTS", "항목이 이미 존재합니다.");
    }
}
