package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyPostIdException extends BusinessException {
    public EmptyPostIdException() {
        super(PostErrorCode.EMPTY_POST_ID);
    }
}
