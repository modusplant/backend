package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.GeneralErrorCode;

public class InvalidFileInputException extends BusinessException {
    public InvalidFileInputException() {
        super(GeneralErrorCode.INVALID_FILE_INPUT);
    }
}
