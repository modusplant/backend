package kr.modusplant.legacy.domains.common.error;

import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;

import java.util.List;

public class DataPairOrderMismatchException extends InvalidDataException {
    public DataPairOrderMismatchException(ErrorCode errorCode, List<String> dataNames) {
        super(errorCode, dataNames);
    }
}
