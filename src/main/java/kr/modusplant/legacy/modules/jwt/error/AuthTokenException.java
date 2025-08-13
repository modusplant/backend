package kr.modusplant.legacy.modules.jwt.error;

import kr.modusplant.domain.exception.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;
import lombok.Getter;

@Getter
public class AuthTokenException extends BusinessException {
    public AuthTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
