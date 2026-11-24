package kr.modusplant.domains.record.domain.exception;

import kr.modusplant.domains.record.domain.exception.enums.RecordErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class UnsupportedVideoCodecException extends BusinessException {
    public UnsupportedVideoCodecException() {
        super(RecordErrorCode.UNSUPPORTED_VIDEO_CODEC);
    }
}
