package kr.modusplant.domains.common.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;

public class UnsupportedFileException extends BusinessException {
  public UnsupportedFileException() { super(ErrorCode.UNSUPPORTED_FILE); }
}
