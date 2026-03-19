package kr.modusplant.infrastructure.security.exception;

import kr.modusplant.shared.exception.supers.ErrorCode;

public class AccountStateException extends BusinessAuthenticationException {

  public AccountStateException(ErrorCode errorCode) {
    super(errorCode);
  }

  public AccountStateException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
