package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class ContentProcessingException extends BusinessException {
    public ContentProcessingException() {
        super(PostErrorCode.CONTENT_PROCESSING_FAILED);
    }
}
