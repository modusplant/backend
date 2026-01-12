package kr.modusplant.infrastructure.security.exception;

import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;

public class AccountStateException extends BusinessAuthenticationException {

  public AccountStateException(SecurityErrorCode errorCode) {
    super(errorCode);
  }

  public AccountStateException(SecurityErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
