package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class AuthorNotFoundException extends BusinessException {
    public AuthorNotFoundException() {
        super(PostErrorCode.AUTHOR_NOT_FOUND);
    }
}
