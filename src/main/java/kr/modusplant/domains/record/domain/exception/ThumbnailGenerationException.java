package kr.modusplant.domains.record.domain.exception;

import kr.modusplant.domains.record.domain.exception.enums.RecordErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class ThumbnailGenerationException extends BusinessException {
    public ThumbnailGenerationException() {
        super(RecordErrorCode.THUMBNAIL_GENERATION_FAILED);
    }
}
