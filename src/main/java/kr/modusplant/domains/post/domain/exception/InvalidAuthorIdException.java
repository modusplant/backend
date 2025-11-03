package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidAuthorIdException extends BusinessException {
    public InvalidAuthorIdException() {
        super(PostErrorCode.INVALID_AUTHOR_ID);
    }
}
