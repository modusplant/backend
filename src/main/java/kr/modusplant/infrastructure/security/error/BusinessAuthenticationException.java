package kr.modusplant.infrastructure.security.error;

import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class BusinessAuthenticationException extends AuthenticationException {

  private final SecurityErrorCode errorCode;

  public BusinessAuthenticationException(SecurityErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public BusinessAuthenticationException(SecurityErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
