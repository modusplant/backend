package kr.modusplant.domains.post.framework.out.processor.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;

public class UnsupportedFileException extends BusinessException {
  public UnsupportedFileException() { super(ErrorCode.UNSUPPORTED_FILE); }
}
