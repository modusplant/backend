package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyPostContentException extends BusinessException {
    public EmptyPostContentException() {
        super(PostErrorCode.EMPTY_POST_CONTENT);
    }

    public EmptyPostContentException(String message) {
        super(PostErrorCode.EMPTY_POST_CONTENT, message);
    }
}
