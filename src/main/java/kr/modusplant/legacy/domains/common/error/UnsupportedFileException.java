package kr.modusplant.legacy.domains.common.error;

import kr.modusplant.global.error.BusinessException;
import kr.modusplant.infrastructure.exception.enums.ErrorCode;

public class UnsupportedFileException extends BusinessException {
  public UnsupportedFileException() { super(ErrorCode.UNSUPPORTED_FILE); }
}
