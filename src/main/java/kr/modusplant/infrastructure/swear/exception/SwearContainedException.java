package kr.modusplant.infrastructure.swear.exception;

import kr.modusplant.infrastructure.swear.exception.enums.SwearErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class SwearContainedException extends BusinessException {
    public SwearContainedException() {
        super(SwearErrorCode.SWEAR_CONTAINED);
    }
}
