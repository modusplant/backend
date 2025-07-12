package kr.modusplant.domains.common.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.InvalidDataException;

import java.util.List;

public class DataPairOrderMismatchException extends InvalidDataException {
    public DataPairOrderMismatchException(ErrorCode errorCode, List<String> dataNames) {
        super(errorCode, dataNames);
    }
}
