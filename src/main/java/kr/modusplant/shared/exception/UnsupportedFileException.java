package kr.modusplant.shared.exception;

import kr.modusplant.shared.exception.enums.GeneralErrorCode;

public class UnsupportedFileException extends BusinessException {
  public UnsupportedFileException() { super(GeneralErrorCode.UNSUPPORTED_FILE); }
}
