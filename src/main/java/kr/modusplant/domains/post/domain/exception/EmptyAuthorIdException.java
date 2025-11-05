package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyAuthorIdException extends BusinessException {
    public EmptyAuthorIdException() {
        super(PostErrorCode.EMPTY_AUTHOR_ID);
    }
}
