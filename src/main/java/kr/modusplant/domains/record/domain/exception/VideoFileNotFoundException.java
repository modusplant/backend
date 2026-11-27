package kr.modusplant.domains.record.domain.exception;

import kr.modusplant.domains.record.domain.exception.enums.RecordErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class VideoFileNotFoundException extends BusinessException {
    public VideoFileNotFoundException() {
        super(RecordErrorCode.VIDEO_FILE_NOT_FOUND);
    }
}
