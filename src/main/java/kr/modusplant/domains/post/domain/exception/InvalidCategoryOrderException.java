package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidCategoryOrderException extends BusinessException {
    public InvalidCategoryOrderException() {
        super(PostErrorCode.INVALID_CATEGORY_ORDER);
    }
}
