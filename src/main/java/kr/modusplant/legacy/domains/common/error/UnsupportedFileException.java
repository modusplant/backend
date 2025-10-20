package kr.modusplant.legacy.domains.common.error;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;

public class UnsupportedFileException extends BusinessException {
  public UnsupportedFileException() { super(ErrorCode.UNSUPPORTED_FILE); }
}
