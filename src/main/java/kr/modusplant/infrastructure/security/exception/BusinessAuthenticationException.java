package kr.modusplant.infrastructure.security.exception;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class BusinessAuthenticationException extends AuthenticationException {

  private final ErrorCode errorCode;

  public BusinessAuthenticationException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public BusinessAuthenticationException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
