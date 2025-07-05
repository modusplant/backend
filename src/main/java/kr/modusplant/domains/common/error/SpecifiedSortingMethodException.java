package kr.modusplant.domains.common.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;

public class SpecifiedSortingMethodException extends BusinessException {
    public SpecifiedSortingMethodException() { super(ErrorCode.SPECIFIED_SORTING_METHOD); }
}
