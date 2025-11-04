package kr.modusplant.domains.normalidentity.normal.domain.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.supers.ResponseCode;

public class InvalidValueException extends BusinessException {
  public InvalidValueException(ResponseCode errorCode) {
    super(errorCode);
  }

  public InvalidValueException(ResponseCode errorCode, String message) {
    super(errorCode, message);
  }
}
