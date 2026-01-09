package kr.modusplant.domains.account.normal.domain.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.supers.ErrorCode;

public class InvalidValueException extends BusinessException {
  public InvalidValueException(ErrorCode errorCode) {
    super(errorCode);
  }

  public InvalidValueException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
