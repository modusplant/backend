package kr.modusplant.framework.aws.exception;

import kr.modusplant.framework.aws.exception.enums.AWSErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class NotFoundFileKeyOnS3Exception extends BusinessException {
    public NotFoundFileKeyOnS3Exception() { super(AWSErrorCode.NOT_FOUND_FILE_KEY_ON_S3); }
}
