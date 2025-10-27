package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyPostStatusException extends BusinessException {
    public EmptyPostStatusException() {
        super(PostErrorCode.EMPTY_POST_STATUS);
    }
}
