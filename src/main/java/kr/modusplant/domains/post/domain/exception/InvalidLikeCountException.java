package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidLikeCountException extends BusinessException {
    public InvalidLikeCountException() {
        super(PostErrorCode.INVALID_LIKE_COUNT);
    }
}
