package kr.modusplant.legacy.domains.common.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.InvalidDataException;

import java.util.List;

public class DataPairNumberMismatchException extends InvalidDataException {

  public DataPairNumberMismatchException(ErrorCode errorCode, List<String> dataNames) {
    super(errorCode, dataNames);
  }
}
