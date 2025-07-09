package kr.modusplant.domains.common.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;

public class InvalidPaginationRangeException extends BusinessException {
  public InvalidPaginationRangeException() {
    super(ErrorCode.INVALID_PAGE_RANGE);
  }
}
