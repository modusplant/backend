package kr.modusplant.legacy.domains.common.error;

import kr.modusplant.domain.exception.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;
import lombok.Getter;

@Getter
public class EmptyValueException extends BusinessException {

    private final String emptyData;

    public EmptyValueException(ErrorCode errorCode, String emptyData) {
        super(errorCode);
        this.emptyData = emptyData;
    }
}
