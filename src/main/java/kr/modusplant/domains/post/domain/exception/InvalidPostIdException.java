package kr.modusplant.domains.post.domain.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidPostIdException extends BusinessException {
        public InvalidPostIdException() {
            super(PostErrorCode.INVALID_POST_ID);
        }
}
