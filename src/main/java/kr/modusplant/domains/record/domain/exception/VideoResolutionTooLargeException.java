package kr.modusplant.domains.record.domain.exception;

import kr.modusplant.domains.record.domain.exception.enums.RecordErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class VideoResolutionTooLargeException extends BusinessException {
    public VideoResolutionTooLargeException() {
        super(RecordErrorCode.VIDEO_RESOLUTION_TOO_LARGE);
    }
}
