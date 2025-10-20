package kr.modusplant.legacy.domains.common.error;

import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;

import java.util.List;

public class DataPairNumberMismatchException extends InvalidDataException {

  public DataPairNumberMismatchException(ErrorCode errorCode, List<String> dataNames) {
    super(errorCode, dataNames);
  }
}
