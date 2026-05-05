package kr.modusplant.domains.post.framework.out.processor.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class TextFileOverLengthException extends BusinessException {
    public TextFileOverLengthException() {
        super(PostErrorCode.TEXT_FILE_OVER_LENGTH);
    }
}