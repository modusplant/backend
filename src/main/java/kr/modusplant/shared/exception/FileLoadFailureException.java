package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.GeneralErrorCode;

public class FileLoadFailureException extends BusinessException {
    public FileLoadFailureException() {
        super(GeneralErrorCode.FILE_LOAD_FAILURE);
    }
}
