package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyCategoryIdException extends BusinessException {
    public EmptyCategoryIdException() {
        super(PostErrorCode.EMPTY_CATEGORY_ID);
    }
}
