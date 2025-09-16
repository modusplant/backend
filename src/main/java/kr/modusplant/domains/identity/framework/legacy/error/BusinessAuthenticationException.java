package kr.modusplant.domains.identity.framework.legacy.error;

import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class BusinessAuthenticationException extends AuthenticationException {

  private final IdentityErrorCode errorCode;

  public BusinessAuthenticationException(IdentityErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public BusinessAuthenticationException(IdentityErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
