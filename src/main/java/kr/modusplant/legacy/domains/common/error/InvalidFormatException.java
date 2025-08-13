package kr.modusplant.legacy.domains.common.error;

import kr.modusplant.global.error.BusinessException;
import kr.modusplant.infrastructure.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidFormatException extends BusinessException {

    private final String invalidData;

    public InvalidFormatException(String invalidData) {
        super(ErrorCode.GENERIC_ERROR);
        this.invalidData = invalidData;
    }
}
