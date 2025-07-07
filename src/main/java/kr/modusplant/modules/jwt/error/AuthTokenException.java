package kr.modusplant.modules.jwt.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;
import lombok.Getter;

@Getter
public class AuthTokenException extends BusinessException {
    public AuthTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
