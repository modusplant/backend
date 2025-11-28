package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyCategoryNameException extends BusinessException {
    public EmptyCategoryNameException() {
        super(PostErrorCode.EMPTY_CATEGORY_NAME);
    }
}
