package kr.modusplant.legacy.modules.jwt.error;

import kr.modusplant.infrastructure.exception.BusinessException;
import kr.modusplant.infrastructure.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AuthTokenException extends BusinessException {
    public AuthTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
