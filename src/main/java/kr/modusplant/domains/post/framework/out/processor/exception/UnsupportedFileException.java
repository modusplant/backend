package kr.modusplant.domains.post.framework.out.processor.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class UnsupportedFileException extends BusinessException {
  public UnsupportedFileException() { super(PostErrorCode.UNSUPPORTED_FILE); }
}
