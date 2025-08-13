package kr.modusplant.legacy.modules.jwt.error;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AuthTokenException extends BusinessException {
    public AuthTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
