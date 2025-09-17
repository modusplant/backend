package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidCategoryIdException extends BusinessException {
    public InvalidCategoryIdException() {
        super(PostErrorCode.INVALID_CATEGORY_ID);
    }
}
