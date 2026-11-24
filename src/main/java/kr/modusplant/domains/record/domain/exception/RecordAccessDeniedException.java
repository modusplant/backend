package kr.modusplant.domains.record.domain.exception;

import kr.modusplant.domains.record.domain.exception.enums.RecordErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class RecordAccessDeniedException extends BusinessException {
    public RecordAccessDeniedException() {
        super(RecordErrorCode.RECORD_ACCESS_DENIED);
    }
}
