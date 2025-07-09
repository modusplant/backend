package kr.modusplant.global.error;

import kr.modusplant.global.enums.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidDataException extends BusinessException {

    private final String invalidDataName;

    public InvalidDataException(ErrorCode errorCode, String invalidDataName) {
        super(errorCode);
        this.invalidDataName = invalidDataName;
    }
}
