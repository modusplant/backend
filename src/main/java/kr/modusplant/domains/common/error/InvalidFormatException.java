package kr.modusplant.domains.common.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;
import lombok.Getter;

@Getter
public class InvalidFormatException extends BusinessException {

    private final String invalidData;

    public InvalidFormatException(String invalidData) {
        super(ErrorCode.INVALID_FORMAT);
        this.invalidData = invalidData;
    }
}
