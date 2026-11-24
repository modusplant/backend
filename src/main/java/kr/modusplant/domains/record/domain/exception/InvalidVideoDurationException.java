package kr.modusplant.domains.record.domain.exception;

import kr.modusplant.domains.record.domain.exception.enums.RecordErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class InvalidVideoDurationException extends BusinessException {
    public InvalidVideoDurationException() {
        super(RecordErrorCode.INVALID_VIDEO_DURATION);
    }
}
