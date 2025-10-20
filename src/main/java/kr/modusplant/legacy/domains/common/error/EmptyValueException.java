package kr.modusplant.legacy.domains.common.error;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class EmptyValueException extends BusinessException {

    private final String emptyData;

    public EmptyValueException(ErrorCode errorCode, String emptyData) {
        super(errorCode);
        this.emptyData = emptyData;
    }
}
