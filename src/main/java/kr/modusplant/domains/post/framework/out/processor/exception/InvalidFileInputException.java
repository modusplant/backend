package kr.modusplant.domains.post.framework.out.processor.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidFileInputException extends BusinessException {
    public InvalidFileInputException() {
        super(PostErrorCode.INVALID_FILE_INPUT);
    }
}
