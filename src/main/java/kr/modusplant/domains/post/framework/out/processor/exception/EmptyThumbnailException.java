package kr.modusplant.domains.post.framework.out.processor.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyThumbnailException extends BusinessException {
    public EmptyThumbnailException() {
        super(PostErrorCode.EMPTY_THUMBNAIL);
    }
}
