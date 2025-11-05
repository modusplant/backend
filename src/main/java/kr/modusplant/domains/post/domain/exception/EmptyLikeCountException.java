package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class EmptyLikeCountException extends BusinessException {
    public EmptyLikeCountException() {
        super(PostErrorCode.EMPTY_LIKE_COUNT);
    }
}
