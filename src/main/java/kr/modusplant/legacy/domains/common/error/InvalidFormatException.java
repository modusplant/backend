package kr.modusplant.legacy.domains.common.error;

import kr.modusplant.domain.exception.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;
import lombok.Getter;

@Getter
public class InvalidFormatException extends BusinessException {

    private final String invalidData;

    public InvalidFormatException(String invalidData) {
        super(ErrorCode.GENERIC_ERROR);
        this.invalidData = invalidData;
    }
}
