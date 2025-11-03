package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidPostStatusException extends BusinessException {
    public InvalidPostStatusException() {
        super(PostErrorCode.INVALID_POST_STATUS);
    }
}
