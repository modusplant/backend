package kr.modusplant.domains.post.framework.out.processor.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;

public class InvalidFileInputException extends BusinessException {
    public InvalidFileInputException() {
        super(ErrorCode.INVALID_FILE_INPUT);
    }
}
