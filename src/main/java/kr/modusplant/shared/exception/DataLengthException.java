package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.supers.ErrorCode;
import lombok.Getter;

@Getter
public class DataLengthException extends BusinessException {
    public DataLengthException(ErrorCode errorCode) {
        super(errorCode);
    }
}