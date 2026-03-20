package kr.modusplant.domains.post.framework.out.processor.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class ThumbnailNotAllowedException extends BusinessException {
    public ThumbnailNotAllowedException() {
        super(PostErrorCode.THUMBNAIL_NOT_ALLOWED);
    }
}
