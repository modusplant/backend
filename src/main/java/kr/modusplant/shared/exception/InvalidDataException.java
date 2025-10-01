package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import lombok.Getter;

import java.util.List;

@Getter
public class InvalidDataException extends BusinessException {

    private final String dataName;
    private final List<String> dataNames;

    public InvalidDataException(ResponseCode errorCode, String dataName) {
        super(errorCode);
        this.dataName = dataName;
        this.dataNames = null;
    }

    public InvalidDataException(ResponseCode errorCode, List<String> dataNames) {
        super(errorCode);
        this.dataName = null;
        this.dataNames = dataNames;
    }
}
