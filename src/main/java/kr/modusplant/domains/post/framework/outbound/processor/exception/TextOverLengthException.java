package kr.modusplant.domains.post.framework.outbound.processor.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class TextOverLengthException extends BusinessException {
    public TextOverLengthException() {
        super(PostErrorCode.TEXT_FILE_OVER_LENGTH);
    }
}