package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidPostContentException extends BusinessException {
    public InvalidPostContentException() {
        super(PostErrorCode.INVALID_POST_CONTENT);
    }

    public InvalidPostContentException(String message) {
        super(PostErrorCode.INVALID_POST_CONTENT, message);
    }
}
