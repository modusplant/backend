package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.GeneralErrorCode;

public class FileLimitExceededException extends BusinessException {
    public FileLimitExceededException() {
        super(GeneralErrorCode.FILE_LIMIT_EXCEEDED);
    }
}
