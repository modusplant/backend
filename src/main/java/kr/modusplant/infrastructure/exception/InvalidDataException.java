package kr.modusplant.infrastructure.exception;

import kr.modusplant.infrastructure.exception.enums.ErrorCode;
import lombok.Getter;

import java.util.List;

@Getter
public class InvalidDataException extends BusinessException {

    private final String dataName;
    private final List<String> dataNames;

    public InvalidDataException(ErrorCode errorCode, String dataName) {
        super(errorCode);
        this.dataName = dataName;
        this.dataNames = null;
    }

    public InvalidDataException(ErrorCode errorCode, List<String> dataNames) {
        super(errorCode);
        this.dataName = null;
        this.dataNames = dataNames;
    }
}
