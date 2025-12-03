package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class PostAccessDeniedException extends BusinessException {
    public PostAccessDeniedException() {
        super(PostErrorCode.POST_ACCESS_DENIED);
    }
}
