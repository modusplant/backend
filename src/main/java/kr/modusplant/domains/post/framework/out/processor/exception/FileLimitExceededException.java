package kr.modusplant.domains.post.framework.out.processor.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;

public class FileLimitExceededException extends BusinessException {
    public FileLimitExceededException() {
        super(ErrorCode.FILE_LIMIT_EXCEEDED);
    }
}
