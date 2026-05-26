package kr.modusplant.shared.framework.aws.exception;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.framework.aws.exception.enums.AWSErrorCode;

public class NotFoundFileKeyOnS3Exception extends BusinessException {
    public NotFoundFileKeyOnS3Exception() { super(AWSErrorCode.NOT_FOUND_FILE_KEY_ON_S3); }
}
