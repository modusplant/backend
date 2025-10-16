package kr.modusplant.infrastructure.jwt.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AuthTokenException extends BusinessException {
    public AuthTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
