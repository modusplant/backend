package kr.modusplant.legacy.domains.common.error;

import kr.modusplant.infrastructure.exception.InvalidDataException;
import kr.modusplant.infrastructure.exception.enums.ErrorCode;

import java.util.List;

public class DataPairNumberMismatchException extends InvalidDataException {

  public DataPairNumberMismatchException(ErrorCode errorCode, List<String> dataNames) {
    super(errorCode, dataNames);
  }
}
