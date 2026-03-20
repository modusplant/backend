package kr.modusplant.domains.post.framework.out.processor.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidThumbnailException extends BusinessException {
    public InvalidThumbnailException() {
        super(PostErrorCode.INVALID_THUMBNAIL);
    }
}
