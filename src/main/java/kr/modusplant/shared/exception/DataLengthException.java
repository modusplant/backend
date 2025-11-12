package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import lombok.Getter;

@Getter
public class DataLengthException extends BusinessException {
    public DataLengthException(ResponseCode errorCode) {
        super(errorCode);
    }
}